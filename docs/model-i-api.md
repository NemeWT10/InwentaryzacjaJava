# Model danych i API

## Encje

| Encja | Pola |
|---|---|
| `User` | `id`, `login`, `passwordHash`, `role` (`STUDENT`, `LECTURER`, `ADMIN`) |
| `Room` | `id`, `number`, `description` |
| `Workstation` | `id`, `room`, `number`, `notes` |
| `Equipment` | `id`, `workstation`, `type` (`BOARD`, `CABLE`, `PROGRAMMER`, `MODULE`), `name`, `microcontroller` (tylko płytki), `status` (`WORKING`, `BROKEN`, `IN_REPAIR`) |
| `FaultReport` | `id`, `workstation`, `reporter` (`User`), `description`, `status` (`OPEN`, `CLOSED`), `createdAt` |

## Endpointy

| Metoda i ścieżka                                                           | Rola |
|----------------------------------------------------------------------------|---|
| `POST /api/auth/login` - token JWT + rola                                  | wszyscy |
| `GET /api/rooms`, `/api/workstations`, `/api/equipment`                    | wszyscy |
| `GET /api/equipment?microcontroller=…`                                     | wszyscy |
| `POST /api/workstations`, `PUT /api/workstations/{id}`                     | `LECTURER` |
| `POST /api/equipment`, `PUT/DELETE /api/equipment/{id}`                    | `LECTURER` |
| `POST /api/rooms`                                                          | `ADMIN` |
| `POST /api/fault-reports`                                                  | `STUDENT` |
| `GET /api/fault-reports`, `PUT /api/fault-reports/{id}`                    | `LECTURER` |
| `GET/POST/PUT /api/users`                                                  | `ADMIN` |
| `GET /api/inventory/export?format=json\|xml`, `POST /api/inventory/import` | `LECTURER` |

## Logowanie

- żądanie: `{"login": "...", "password": "..."}`
- odpowiedź `200`: `{"token": "...", "role": "LECTURER"}`, błędne dane: `401`
- kolejne żądania: nagłówek `Authorization: Bearer <token>`
- serwer: `http://localhost:8080`

## Przeglądanie (format JSON)

| Żądanie | Odpowiedź |
|---|---|
| `GET /api/rooms` | wszystkie sale |
| `GET /api/workstations?roomId=1` | stanowiska w sali |
| `GET /api/equipment?workstationId=1` | sprzęt na stanowisku |
| `GET /api/equipment?microcontroller=stm32` | sprzęt, którego mikrokontroler zawiera tekst (bez rozróżniania wielkości liter) |

Odpowiedź `200` = tablica JSON. Relacje jako id (`roomId`, `workstationId`), nie zagnieżdżone obiekty —
ten sam format co w eksporcie. Brak lub zły token: `401`.

```json
[{"id": 1, "number": "E203", "description": "Pracownia systemów wbudowanych"}]
```

```json
[{"id": 1, "roomId": 1, "number": "1", "notes": "Brak zasilacza"}]
```

```json
[{"id": 1, "workstationId": 1, "type": "BOARD", "name": "NUCLEO-L476RG", "microcontroller": "STM32L476RG", "status": "WORKING"}]
```

`User` w odpowiedziach bez `passwordHash`.

## Zapis (wykładowca)

| Żądanie | Treść żądania |
|---|---|
| `POST /api/workstations` | stanowisko, `id` = `null` |
| `PUT /api/workstations/{id}` | całe stanowisko |
| `POST /api/equipment` | sprzęt, `id` = `null` |
| `PUT /api/equipment/{id}` | cały sprzęt |
| `DELETE /api/equipment/{id}` | brak |

Treść w tym samym formacie co przy przeglądaniu (relacje jako id). Sukces = dowolny kod `2xx`
(np. `201` z zapisanym obiektem, `204` po usunięciu) — klient po zapisie pobiera listę od nowa.
Inna rola niż `LECTURER`: `403`. Stanowisk się nie usuwa.

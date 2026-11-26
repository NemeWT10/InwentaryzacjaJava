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
| `POST /api/workstations`, `PUT/DELETE /api/workstations/{id}`              | `LECTURER` |
| `POST /api/equipment`, `PUT/DELETE /api/equipment/{id}`                    | `LECTURER` |
| `POST /api/rooms`, `DELETE /api/rooms/{id}`                                | `ADMIN` |
| `POST /api/fault-reports`                                                  | `STUDENT` |
| `GET /api/fault-reports?status=OPEN`, `PUT /api/fault-reports/{id}`        | `LECTURER` |
| `GET/POST/PUT /api/users`, `DELETE /api/users/{id}`                       | `ADMIN` |
| `GET /api/inventory/export`, `POST /api/inventory/import`                  | `LECTURER`, `ADMIN` |

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
| `DELETE /api/workstations/{id}` | brak; tylko stanowisko bez sprzętu i zgłoszeń, inaczej `409` |
| `POST /api/equipment` | sprzęt, `id` = `null` |
| `PUT /api/equipment/{id}` | cały sprzęt |
| `DELETE /api/equipment/{id}` | brak |

Treść w tym samym formacie co przy przeglądaniu (relacje jako id). Sukces = dowolny kod `2xx`
(np. `201` z zapisanym obiektem, `204` po usunięciu) — klient po zapisie pobiera listę od nowa.
Inna rola niż `LECTURER`: `403`.

## Zgłoszenia usterek

| Żądanie | Rola | Treść żądania |
|---|---|---|
| `POST /api/fault-reports` | `STUDENT` | `{"workstationId": 1, "description": "Nie działa port USB"}` |
| `GET /api/fault-reports?status=OPEN` | `LECTURER` | brak |
| `PUT /api/fault-reports/{id}` | `LECTURER` | `{"status": "CLOSED"}` (zamknięcie zgłoszenia, innych zmian nie ma) |

Przy `POST` serwer sam ustawia zgłaszającego (użytkownik z tokenu), `status` = `OPEN` i `createdAt`.
Odpowiedź `GET` — z numerami sali i stanowiska oraz loginem zgłaszającego (do wyświetlenia na liście):

```json
[{"id": 1, "workstationId": 1, "roomNumber": "E203", "workstationNumber": "1", "reporterLogin": "student",
  "description": "Nie działa port USB", "status": "OPEN", "createdAt": "2026-11-12T10:15:00Z"}]
```

## Administracja

| Żądanie | Treść żądania |
|---|---|
| `POST /api/rooms` | `{"number": "E204", "description": "Pracownia systemów wbudowanych"}` |
| `DELETE /api/rooms/{id}` | brak; tylko sala bez stanowisk, inaczej `409` |
| `GET /api/users` | brak; odpowiedź: `[{"id": 1, "login": "admin", "role": "ADMIN"}]` |
| `POST /api/users` | `{"login": "jkowalski", "password": "haslo123", "role": "STUDENT"}` |
| `PUT /api/users/{id}` | jak przy `POST`; `"password": null` = hasło bez zmian |
| `DELETE /api/users/{id}` | brak; nie własne konto, nie ostatni administrator i nie konto ze zgłoszeniami usterek, inaczej `409` |

Zajęty numer sali lub login: `409`. Ostatniemu administratorowi nie można odebrać roli (`409`).
Inna rola niż `ADMIN`: `403`.
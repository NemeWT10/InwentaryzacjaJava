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
| `POST/PUT/DELETE /api/workstations`, `/api/equipment`                      | `LECTURER` |
| `POST /api/rooms`                                                          | `ADMIN` |
| `POST /api/fault-reports`                                                  | `STUDENT` |
| `GET /api/fault-reports`, `PUT /api/fault-reports/{id}`                    | `LECTURER` |
| `GET/POST/PUT /api/users`                                                  | `ADMIN` |
| `GET /api/inventory/export?format=json\|xml`, `POST /api/inventory/import` | `LECTURER` |

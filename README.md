# System inwentaryzacji płytek ewaluacyjnych

Aplikacja do ewidencji sprzętu pracowni systemów wbudowanych (płytki, kable, programatory, moduły)
z przypisaniem do sal i stanowisk oraz z podziałem na role: student, wykładowca, administrator.

Architektura klient-serwer: klient JavaFX komunikuje się z backendem Spring Boot przez REST API (JWT).

## Struktura repozytorium

| Folder | Zawartość |
|---|---|
| `client/` | aplikacja kliencka JavaFX |
| `server/` | backend Spring Boot + MySQL |
| `docs/` | model danych i lista endpointów (`model-i-api.md`) |
| `dane/` | dane przykładowe do importu (`inwentarz-przykladowy.json`) |

## Autorzy

- **Michał Władziński** — backend (Spring Boot, baza danych, REST API, Spring Security + JWT, OpenAPI)
- **Szymon Wójcik** — klient JavaFX, obsługa JWT w kliencie, import/eksport JSON i XML, dane przykładowe, README

## Wymagania

- JDK 25
- Gradle — przez dołączony wrapper (`gradlew` / `gradlew.bat`), bez osobnej instalacji
- Docker z Docker Compose (baza MySQL)

## Uruchomienie

Klient: `gradlew :client:run` (w IntelliJ: panel Gradle → `client` → Tasks → application → run).

Baza danych (przed serwerem): `docker compose up -d`, stan: `docker ps`.

Serwer: `gradlew :server:bootRun`.

Dokumentacja REST API (Swagger) przy działającym serwerze: http://localhost:8080/swagger-ui.html.
Token z `POST /api/auth/login` wpisuje się w przycisku „Authorize”.

Testy serwera: `gradlew :server:test` (baza H2 w pamięci, Docker nie jest potrzebny).

### Konta startowe

Przy pustej tabeli użytkowników serwer tworzy po jednym koncie na rolę (tylko do testów i prezentacji):

| Login | Hasło | Rola |
|---|---|---|
| `student` | `student123` | `STUDENT` |
| `wykladowca` | `wykladowca123` | `LECTURER` |
| `admin` | `admin123` | `ADMIN` |

Tokeny JWT są podpisywane kluczem `jwt.secret` (domyślny jest tylko do pracy lokalnej, poza nią ustaw zmienną `JWT_SECRET`).

### Dane przykładowe

Plik `dane/inwentarz-przykladowy.json` zawiera 2 sale, 4 stanowiska i 12 sztuk sprzętu (płytki, kable, programatory, moduły).
Wczytanie: zaloguj się jako `wykladowca`, kliknij **Import** i wybierz plik.

Import zawsze dodaje nowe rekordy, a numery sal muszą być wolne, dlatego plik wczytuje się raz, do bazy bez sal E201 i E202.
Ponowny import kończy się błędem `409` i niczego nie zapisuje.

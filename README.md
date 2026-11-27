# Inwentaryzacja sprzętu laboratoriów systemów wbudowanych

Aplikacja do ewidencji sprzętu pracowni systemów wbudowanych (płytki, kable, programatory, moduły)
z przypisaniem do sal i stanowisk oraz z podziałem na role: student, wykładowca, administrator.

Architektura klient-serwer: klient JavaFX komunikuje się z backendem Spring Boot przez REST API (JSON, token JWT),
a serwer zapisuje dane w bazie MySQL.

## Technologie

| Część | Technologie |
|---|---|
| wspólne | Java 25, Gradle 9.7.1 (wrapper, projekt wielomodułowy), Git + GitHub |
| klient | JavaFX 25.0.1 (interfejs w kodzie, bez FXML, wygląd w CSS), `java.net.http.HttpClient`, Jackson 3.1.5 (JSON i XML), JUnit 6 |
| serwer | Spring Boot 4.1.2 (Web MVC, Data JPA / Hibernate, Validation), Spring Security + JWT (HS256, hasła BCrypt), MySQL 8.0 w Dockerze, springdoc-openapi 3.2.0 (Swagger UI), Lombok |
| testy serwera | JUnit, Mockito, MockMvc, baza H2 w pamięci |

## Struktura repozytorium

| Folder | Zawartość |
|---|---|
| `client/` | aplikacja kliencka JavaFX |
| `server/` | backend Spring Boot |
| `dane/` | dane przykładowe do importu (`inwentarz-przykladowy.json`) |

## Autorzy

- **Michał Władziński** - backend (Spring Boot, baza danych, REST API, Spring Security + JWT, OpenAPI)
- **Szymon Wójcik** - klient JavaFX, obsługa JWT w kliencie, import/eksport JSON i XML, dane przykładowe, README

## Wymagania środowiskowe

- JDK 25
- Gradle - przez dołączony wrapper (`gradlew` / `gradlew.bat`), bez osobnej instalacji
- Docker z Docker Compose (baza MySQL)

## Uruchomienie

Baza danych (przed serwerem): `docker compose up -d`, stan: `docker ps`.

Serwer: `gradlew :server:bootRun` (adres `http://localhost:8080`).

Klient: `gradlew :client:run` (w IntelliJ: panel Gradle - `client` - Tasks - application - run).

### Konta startowe

Przy pustej tabeli użytkowników serwer tworzy po jednym koncie na rolę (tylko do testów i prezentacji):

| Login | Hasło | Rola |
|---|---|---|
| `student` | `student123` | `STUDENT` |
| `wykladowca` | `wykladowca123` | `LECTURER` |
| `admin` | `admin123` | `ADMIN` |

Tokeny JWT są podpisywane kluczem `jwt.secret` (domyślny jest tylko do pracy lokalnej, poza nią ustaw zmienną `JWT_SECRET`).
Token jest ważny 8 godzin.

## Role

| Rola | Uprawnienia                                                                                                                         |
|---|-------------------------------------------------------------------------------------------------------------------------------------|
| student | przegląda sale, stanowiska i sprzęt, szuka sprzętu po mikrokontrolerze, zgłasza usterkę stanowiska                                  |
| wykładowca | przegląda i szuka jak student, dodaje, edytuje i usuwa stanowiska i sprzęt, przegląda i zamyka zgłoszenia usterek, import i eksport |
| administrator | przegląda i szuka jak student, dodaje i usuwa sale, zarządza kontami (dodawanie, edycja, usuwanie), import i eksport                |

Każda rola ma jeden wspólny ekran, przyciski akcji pokazują się zależnie od roli. Serwer sprawdza rolę z tokenu
przy każdym żądaniu: brak lub zły token daje `401`, zła rola `403`.

## Model danych

| Encja | Pola | Powiązania                                |
|---|---|-------------------------------------------|
| `User` | `login` (unikalny), `passwordHash`, `role` (`STUDENT`, `LECTURER`, `ADMIN`) | -                                         |
| `Room` | `number` (unikalny), `description` | -                                         |
| `Workstation` | `number` (unikalny w sali), `notes` | należy do sali                            |
| `Equipment` | `type` (`BOARD`, `CABLE`, `PROGRAMMER`, `MODULE`), `name`, `microcontroller`, `status` (`WORKING`, `BROKEN`, `IN_REPAIR`) | należy do stanowiska                      |
| `FaultReport` | `description`, `status` (`OPEN`, `CLOSED`), `createdAt` | dotyczy stanowiska, zgłoszona przez konto |

W API i w plikach eksportu powiązania są zapisane jako id (`roomId`, `workstationId`), a konta nie zawierają hasła.

Reguły pilnowane przez serwer (błąd ma w polu `detail` komunikat po polsku, który klient pokazuje):

- mikrokontroler podaje się tylko dla płytek (`BOARD`), inaczej `400`;
- zajęty numer sali, numer stanowiska w sali lub login: `409`;
- salę można usunąć tylko bez stanowisk, a stanowisko tylko bez sprzętu i zgłoszeń (`409`);
- konta nie można usunąć, jeśli jest własne, jest ostatnim administratorem albo ma zgłoszenia; ostatniemu administratorowi
  nie można też odebrać roli (`409`);
- przy zgłoszeniu usterki serwer sam ustawia zgłaszającego (konto z tokenu), status `OPEN` i datę.

## REST API (Swagger)

Pełna lista endpointów z rolami, parametrami i formatami danych jest w Swagger UI przy działającym serwerze:
http://localhost:8080/swagger-ui.html (opis OpenAPI w JSON: `/v3/api-docs`).
Token z `POST /api/auth/login` wpisuje się w przycisku „Authorize”.

## Import i eksport

Wykładowca i administrator mogą zapisać cały inwentarz (sale, stanowiska, sprzęt) do pliku i wczytać go z pliku.
Serwer przyjmuje i zwraca JSON (`/api/inventory/export`, `/api/inventory/import`), a format pliku wybiera klient
po rozszerzeniu: `.xml` to XML, każde inne to JSON.

Import zastępuje cały inwentarz: obecne sale, stanowiska, sprzęt i zgłoszenia usterek są usuwane, konta użytkowników zostają.
Przed importem klient prosi o potwierdzenie. Błąd w pliku cofa całą operację, więc dane w bazie zostają bez zmian.

### Dane przykładowe

Plik `dane/inwentarz-przykladowy.json` zawiera 2 sale, 4 stanowiska i 12 sztuk sprzętu (płytki, kable, programatory, moduły).

## Testy

| Moduł | Polecenie | Testy |
|---|---|---|
| klient | `gradlew :client:test` | 8 jednostkowych (zapis i odczyt JSON/XML, pliki), 7 integracyjnych (`ApiClient` z atrapą serwera HTTP: logowanie, token, treść żądań, komunikaty błędów) |
| serwer | `gradlew :server:test` | 26 jednostkowych (serwisy z Mockito, tokeny JWT), 26 integracyjnych (MockMvc na całej aplikacji, baza H2) |

Testy nie potrzebują Dockera ani działającego serwera.

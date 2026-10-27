# System inwentaryzacji płytek ewaluacyjnych

Aplikacja do ewidencji sprzętu pracowni systemów wbudowanych (płytki, kable, programatory, moduły)
z przypisaniem do sal i stanowisk oraz z podziałem na role: student, wykładowca, administrator.

Architektura klient-serwer: klient JavaFX komunikuje się z backendem Spring Boot przez REST API (JWT).

## Struktura repozytorium

| Folder | Zawartość |
|---|---|
| `client/` | aplikacja kliencka JavaFX (planowana) |
| `server/` | backend Spring Boot + PostgreSQL (planowany) |

## Autorzy

- **Michał Władziński** — backend (Spring Boot, baza danych, REST API, Spring Security + JWT, OpenAPI)
- **Szymon Wójcik** — klient JavaFX, obsługa JWT w kliencie, import/eksport JSON i XML, dane przykładowe, README

## Wymagania

- JDK 25
- Gradle — przez dołączony wrapper (`gradlew` / `gradlew.bat`), bez osobnej instalacji

Program zaliczeniowy z SKJ
Wersja 1.0

Autor: Patryk Wąsiewicz, S11671

1. OPIS URUCHOMIENIA
Program uruchamiamy skryptem "run.sh", który odpala odpowiedniego JAR'a z
podanymi argumentami. Możliwe parametry:
    --port [numer_portu] 
        Numer portu, na którym uruchomiony będzie serwer. Wymagany.
    --light
        Flaga, która określa czy program ma zostać uruchominy w trybie
"LEKKIM". Wtedy będze filtrował dane tylko do żądań tekstowych. Opcjonalny.

Przykłady wywołania:
./run.sh --port 10000 --light
./run.sh --port 10000 --light

2. ARCHITEKTURA
Opis architektury znajduje się w pliku docs/Architecture.odt

3. BINARIA
Skompilowany plik jar znajduje się w katalogu
out/artifacts/SKJ_ServerProxy_jar.

4. ŹRÓDŁA
Projekt został zrealizowany w IntelijIdea (SKJ_Server.eml). Został jednak
eksportowany do Eclipse, więc nie powinno być problemów z jego uruchomieniem
W katalogu znajduje się również repozytorium GITa, więc można podejrzać
wszystkie zmiany w projekcie.

5. BIBLIOTEKI
guice - kontener typów do realizacji IoC
args4j - parsowanie argumentów
jsoup - parsowanie html (niestety nie zdążyłem zająć się dodatkowym
podpunktem, ale zacząłem).

6. PROBLEMY
    a. Nie jest wspierany Keep-Alive. Dla każdego requestu tworzone jest
nowe połączenie. Przez to strona np. http://stackoverflow.com nie działa - nie
są ładowane skrypty w odpowiedniej kolejności. Próba realizacji ten koncepcji
znajduje się w branch-u konnkeep.
(Szybkie przełączenie so gałęzi: git checkout konnkeep --force)

    b. Dodatkowy podpunkt nie został zrealizowany. Brakło mi niestety czasu.
Szczątki rozpoczęcia tej realizacji znajduje sie w branch-u konnkeep.

7. LOGOWANIE BŁĘDÓW
Aplikacja korzysta z standardowego framework'u do logowania błędów w
aplikacjiach java. Więcej o konfiguracji itd:
http://www.onjava.com/pub/a/onjava/2002/06/19/log.html
Domyślnie aplikacja loguje do konsoli.

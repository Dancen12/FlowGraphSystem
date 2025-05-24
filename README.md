# FlowGraphSystem

FlowGraphSystem jest to system służący do analizy przepływu w sieci przepływowej. System składa się z trzech komponentów. Główny moduł napisany w Javie jest odpowiedzialny za interakcje z użytkownikiem oraz zarządzanie pozostałymi modułami. Następne dwa moduły odpowiadają za wyznaczanie przepływu oraz redukcje cykli. Moduł usuwający cykle został opisany tutaj:  https://github.com/ZaumNort/GraphCycleReducer . Drugi moduł wyznaczający przepływ implementuje algorytmy min-max, Dinitza oraz możliwego przepływu. Całość systemu opisano w pracy naukowej.

## Wymagania systemowe
System wymaga środowiska spełniającego następujące warunki:
- System operacyjny: Windows 64-bit,
- Zainstalowane środowisko Java (JDK) w wersji 21

## Kroki uruchomienia
Aby uruchomić system, należy wykonać poniższe kroki:
1. Pobrać projekt z niniejszego repozytorum oraz https://github.com/ZaumNort/GraphCycleReducer
2. Skompilować kody aplikacji C++, aby otrzymać pliki wykonywalne .exe.
3. Skompilować kody źródłowe aplikacji Java.
4. Otworzyć plik konfiguracyjny cppAppPaths.properties i wpisać pełne ścieżki do plików
   wykonywalnych aplikacji C++.
5. Uruchomić aplikację Java.

## GUI

### Oznaczenia na grafie 
- Zielony wierzchołek - Źródło sieci.
- Czerwony wierzchołek - Ujście sieci.
- Niebieska krawędź - Na krawędzi występuje możliwy przepływ. 
- Żółta krawędź - Na krawędzi występuje minimalny przepływ.
- Zielona krawędź - Na krawędzi występuje maksymalny przepływ.
- Czerwona krawędź - Na krawędzi występuje niepoprawny przepływ.
- Etykieta na krawędzi - Wartości na etykiecie oznaczają od lewej odpowiednio dolne ograniczenie, obecny przeplyw, pojemnosc.

### Przyciski w GUI

- Load Graph Przycisk otwiera okienko w którym można wybrać plik tekstowy z grafem. Po wyborze poprawnego pliku rysowany jest graf.
- Save Graph Przycisk otwiera okienko w którym można wybrać plik do którego zostanie zapisany graf.
- Refresh Przycisk ponownie rysuje załadowany graf.
- Edit Przycisk włącza/wyłącza tryb edycji.
- Min Flow Przycisk uruchamia algorytmy wyznaczające minimalny przepływ. Po zakończeniu obliczeń sieć jest ponownie rysowana.
- Max Flow Przycisk uruchamia algorytmy wyznaczające maksymalny przepływ. Przed wykonaniem faktycznego algorytmu wywoływany jest algorytm możliwego przepływu. Po zakończeniu obliczeń sieć jest ponownie rysowana.
- Feasible flow Przycisk uruchamia algorytmy wyznaczające możliwy przepływ. Po zakończeniu obliczeń sieć jest ponownie rysowana.
- Delete Cycles Przycisk uruchamia algorytmy przekształcające graf w jego acykliczny odpowiednik. Po zakończeniu obliczeń sieć jest ponownie rysowana.

### Edycja grafu
W celu uruchomienia trybu edycji należy wybrać w menu przycisk edit. Po naciśnięciu w formie tabeli zostaną przedstawione wszystkie krawędzie grafu. Dla każdej krawędzi można zmodyfikować wierzchołek z którego krawędź wychodzi, wierzchołek do którego krawędź wchodzi oraz wartości związane z przepływem, takie jak dolne i górne ograniczenie oraz wartość aktualnego przepływu. Istnieje możliwość dodawania i usuwania krawędzi. Modyfikować można również źródło i ujście sieci. Po zakończeniu edycji należy ponownie nacisnąć przycisk edit. Jeśli zmodyfikowany graf jest poprawny, zostanie ponownie narysowany, natomiast w przypadku błędów zostanie wyświetlony stosowny komunikat i będzie trzeba poprawić edytowaną sieć.  



## Struktura danych
- Grafy sa przechowywane w plikach tekstowych
  Ponizej przyklad poprawnie zapisanego grafu:
```bash
4 //liczba wierzcholkow
s //zrodlo
t //ujscie
s 1 1 2 3 //kolejne krawedzie w postaci from, to, l, f, c
s 2 1 2 3
1 t 1 2 3
2 t 1 2 3
```

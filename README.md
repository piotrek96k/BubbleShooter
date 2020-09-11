# Bubble Shooter
Nowa odsłona gry Bubble Shooter  

Minimalna wersja javy: 12  

Uruchomienie:  
Przy wykorzystaniu IDE  
-Eclipse: w ProjectExplorer wybrać BubbleShooter -> prawy przycisk myszy -> Run As -> Maven Build.. -> w polu goals wpisać: javafx:run -> Run  
-IntelliJ: run.. -> Edit Configurations -> Add New Configuration (znaczek +) ->  
Maven -> w polu Working Directory wpisać ścieżkę do projektu, w polu Command Line wpisać: javafx:run -> Run  

Za pomocą konsoli (wymagana instalacja mavena)  
-przejść do folderu /BubbleShooter -> uruchomić aplikację poleceniem: mvn javafx:run  
-zbudować projekt, w folderze /PokemonCards wywołać polecenie mvn package -> przejść do /BubbleShooter/target -> uruchomić aplikację poleceniem java -jar BubbleShooter-0.0.1-SNAPSHOT-jar-with-dependencies.jar 

O grze  

Sterowanie:  
-Celujemy i strzelamy myszką  
-Zmiana kulki jest możliwa poprzez kliknięcie na kulkę do zmiany, lub za pomocą spacji  
-pauza gry za pomocą przycisku z napisem pauza lub klawiszem esc  

Gra posiada dwa tryby:  
-Zręcznościowy  
	w tym trybie mamy do wyboru pięć poziomów trudności, wraz z poziomem trudności rośnie ilość
	kolorów kulek, celem gry jest jej ukończenie w jak najszybszym czasie  
-Przetrwanie  
	w tym trybie gra nie pozwala nam wygrać, a w miarę długości rozgrywki rośnie ilość kolorów kulek,
	celem gry jest osiągnięcie jak najwyższej ilości punktów  

Rodzaje kulek:  
-Zwykła jedno, dwu lub trójkolorowa - kulki te zostają usunięte jeżeli po strzale
ilość stykających się kulek wynosi przynajmniej trzy. Dwu i trójkolorowe kulki działają jak przełączniki
kolorów więc stykające się kulki mogą być w różnych kolorach np. strzelamy dwukolorową kulką 
(czerwono - żółtą) w sąsiadująje kulki czerwoną i żółtą, ilość stykających się kulek które do siebie
pasują wynosi trzy więc dojdzie do ich usunięcia  
-Bomby - usuwają najbliższe kulki nie powodując dalszej ich reakcji (ale mogą aktywować inne bomby)
uruchamiają się jeżeli dojdzie do nich reakcja usuwanych kulek, lub strzelimy w nie kulką
(nie duchem i niszczycielem). Jeżeli strzelamy bombą to zawsze dochodzi do jej wbuchu.  
-Duch - kulka która może przenikać przez inne jeżeli do nich nie pasuje kolorem, aktywuje się
jeżeli znajdzie przynajmniej dwie sąsiadujące pasujące do siebie kulki, których przynajmniej
jeden kolor pasuje do koloru ducha  
-Niszczyciel - najrzadsza dostępna kulka, usuwa wszystkie kulki na swojej drodze, nie powodując 
ich dalszej reakcji, jako jedyna nie może odbijać się od ścian  
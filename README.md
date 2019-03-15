# chasemerpg

19.02 + 20.02 : Arbeit an der Umsetzung der Map als Array damit mehrere Maps vom Mover geblockt (collisioncheck) werden k�nnen
Abgeschlossen - Item Map Block (Map Array) aber noch ausstehend --- Tiles nicht richtig geblockt ? (Analyse mit Analytics und Problem bei Map ItemBlock) ODER collision klappt nicht mit maps[1] (Array wird nicht von Collisioncheck richtig überprüft) ?
21.02 : Collision Check muss überarbeitet werden !
25.02 : Abgeschlossen Collision Check -  Array von checkpoints (AUFPASSEN DASS IN DER GUI DIE ANZAHL DER MAPS ANGEPASST WIRD für Performance und keine Nullpointer)
	    Anfang des Editors
26.02 : Pathfinder erkennt blocked Tiles nicht
09.03 : FIX - collisioncheck weiß wann Map aktiv (moverOnMapTile)-> gibt an welches Tile (x|y) ist und prüft dann - wenn nicht aktiv dann wird nächste Map überprüft
09.03 : Editor Grundgerüst Buttons : Zoom Kamera (+/-), Tile auswählen, Obere Leiste -> Save = text Datei erstellen
        X Pathfinder bei blocked Tiles klappt nicht
        Editor: Mehrere/Einzelne/mit Shift einen Bereich   kann Tiles ändern -> Editor.setTile oder Editor.setRect (Bereich)
10.03 : Idee: Editor unabhängig von GUI -> eigenes Menu, keyListener... erstellen 
12.03 : Aufruf nur vom Editor aus -> als selbstädniges System 
        TODO: Kein immer erneutes Erzeugen des Menus wegen Performance
        Pathfinder weiß auf welcher Map -> Map kann erkennen ob Position auf der Map liegt und erstellt dann den Weg

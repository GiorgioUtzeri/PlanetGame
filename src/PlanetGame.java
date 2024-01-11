import extensions.File;
import extensions.CSVFile;

class PlanetGame extends Program {
    String identifiantSauvegarde = "";
    final char SL = '\n';
    boolean jeuEnCours = true;
    int point_final = 0;
    String chemin = "ressources/baseJoueurs.csv";
    String cheminQuestion = "ressources/questionsNiveau3.csv";
    
    /* Permet d'executer l'ensemble du jeu au démarrage du programme */
    void algorithm(){
        menuAccueil();
    }

// MENU D'ACCUEIL :

    /* permet de convertir un fichier texte dans ressources/ en String */
    String lireTxt(String NomFichier){
        String result = "";
        for(int IDX_LIGNE=0; IDX_LIGNE<rowCount(loadCSV("ressources/"+NomFichier)); IDX_LIGNE++){
            if(!(IDX_LIGNE==rowCount(loadCSV("ressources/"+NomFichier))-1)){
                result += getCell(loadCSV("ressources/"+NomFichier), IDX_LIGNE, 0) + SL; 
            } else {
                result += getCell(loadCSV("ressources/"+NomFichier), IDX_LIGNE, 0);
            }
        }
        return result;
    }

    /* Permet de convertir une chaine de caractère en testant et renvoyer si cela est un chiffre */
    int testerStringToInt(String chaine){
        boolean possible = true;
        String res = "";
        int result = -1;
        int i = 0;
        if(length(chaine)==0){
            possible=false;
        }
        while(possible && i<length(chaine)){
            if(!(charAt(chaine, i)>='0' && charAt(chaine, i)<='9')){
                possible=false;
            } else {
                res+=charAt(chaine, i);
            }
            i++;
        }
        if(possible){
            result = stringToInt(res);
        }
        return result;
    }

    /* Permet d'afficher le menu d'accueil et demande un nombre pour rediriger vers la demande */
    void menuAccueil(){
        jeuEnCours = true;
        while(jeuEnCours){
        println(lireTxt("NomJeu.txt"));
        int NumeroChoisi = -1;
        while(1> NumeroChoisi || NumeroChoisi>4){
            println(SL + "Que veux tu faire ? Merci de chosir un nombre entre 1 et 4");
            NumeroChoisi = testerStringToInt(readString());
        }
        if(NumeroChoisi == 1){
            identifiantSauvegarde = demanderIdentifiant();
            niveau1();
            jeuEnCours = false;
        } else if (NumeroChoisi == 2){
            menuReprise();
            jeuEnCours = false;
        } else if (NumeroChoisi == 3){
            classement();
            jeuEnCours = false;
        } else if (NumeroChoisi == 4){
            jeuEnCours = false;
        }
        }
        
    }

// MENU REPRISE :

    /* Permet de demander l'identifiant d'une partie pour le renvoyer dedans si elle n'était pas finit */
    void menuReprise(){
        jeuEnCours = true;
        while(jeuEnCours){
            println(lireTxt("Continuer.txt") +SL);
            println("Tu souhaites reprendre une partie ? Entre ton identifiant.");
            boolean identifiantBdd=true;
            while(identifiantBdd || jeuEnCours){
                String identifiantPossible = changerString(readString());
                println(identifiantPossible);
                if(equals(identifiantPossible, "Quitter")){
                    jeuEnCours = false;
                    menuAccueil();
                }
                if(chercherIdentifiant(identifiantPossible)){
                    if(verifierNiveau(identifiantPossible)==1){
                        identifiantSauvegarde=identifiantPossible;
                        point_final = chercherPointJoueur(identifiantPossible);
                        enleverJoueur(identifiantPossible);
                        niveau1();
                        jeuEnCours=false;
                    }else if(verifierNiveau(identifiantPossible)==2){
                        identifiantSauvegarde=identifiantPossible;
                        point_final = chercherPointJoueur(identifiantPossible);
                        enleverJoueur(identifiantPossible);
                        niveau2();
                        jeuEnCours=false;
                    }else if(verifierNiveau(identifiantPossible)==3){
                        identifiantSauvegarde=identifiantPossible;
                        point_final = chercherPointJoueur(identifiantPossible);
                        enleverJoueur(identifiantPossible);
                        niveau3();
                        jeuEnCours=false;
                    } else {
                        println("Cette partie est fini merci de selectionner une partie en cours.");
                    }
                } else {
                    println("Merci d'entrer un identifiant d'une partie en cours.");
                }
            }
        }
    }

// MENU CLASSEMENT :

    /* Permet d'afficher le tableau des 5 meilleurs parties dans la base de données */
    String toString(String[][] csv) {
        String table = "";
        int idx_espace = length(getCell(loadCSV("ressources/classement.txt"), 0, 0))/2;
        for (int idxLigne = 0; idxLigne < 6; idxLigne = idxLigne + 1) {
            table+= calculeTableau(idx_espace-20, ' ');
            for (int idxColonne=0; idxColonne<length(csv, 2); idxColonne++){
                if(idxColonne==0){
                    table+=" | ";
                    table+= csv[idxLigne][idxColonne] + genererEspace(csv[idxLigne][idxColonne], 11) +" ";
                } else if (idxColonne==1){
                    table+= csv[idxLigne][idxColonne]+ genererEspace(csv[idxLigne][idxColonne], 12)+" ";
                } else {
                    table+= csv[idxLigne][idxColonne]+ genererEspace(csv[idxLigne][idxColonne], 8)+" ";
                }
                table+="|";
            }
            table+=SL;
            table+= calculeTableau(idx_espace-20, ' ') + calculeTableau(41, '=')+ SL;
        }
        return table;
    }

    /* Permet de convertir une liste entière en String séparer par des espaces */
	String toString(String[] liste){
		String tmp = "";
        for(int i = 0; i<length(liste); i++){
            tmp = tmp + liste[i] + " ";
        }
        return tmp;
	}

    /* Permet de créer un tableau de toutes les parties de la base de données */
    String[][] creerTableauCsv(){
        String[][] tab = new String[rowCount(loadCSV(chemin))+1][columnCount(loadCSV(chemin))];

        for(int IDX_LIGNE=0; IDX_LIGNE<rowCount(loadCSV(chemin)); IDX_LIGNE++){
            for(int IDX_COL=0; IDX_COL<columnCount(loadCSV(chemin)); IDX_COL++){
                if(equals(getCell(loadCSV(chemin), IDX_LIGNE, 1),"0")){
                    tab[IDX_LIGNE][0]=getCell(loadCSV(chemin), IDX_LIGNE, 0);
                    tab[IDX_LIGNE][1]="Jeu Terminé";
                    tab[IDX_LIGNE][2]=getCell(loadCSV(chemin), IDX_LIGNE, 2);
                } else {
                    tab[IDX_LIGNE][IDX_COL]=getCell(loadCSV(chemin), IDX_LIGNE, IDX_COL);
                }
            }
        }

        return tab;
    }

    /* Permet de retourner la valeur d'une case d'un tableau */
    String getChamps(String[][] csv, int idxLigne, int idxChamps) {
        return csv[idxLigne][idxChamps];
    }

    /* Permet de trier un tableau sur la colonne des points */
    void trierSurColonne(String[][] csv) {
        int idxCol=2;
        boolean permutation = true;
        int end = 1;
        while (permutation) {
            permutation = false;
            for (int idxLig = 1; idxLig < length(csv, 1) - 2; idxLig = idxLig +1) {
                if (comparer(getChamps(csv, idxLig,   idxCol),
                            getChamps(csv, idxLig+1, idxCol)) < 0) {
                    permuterLignes(csv, idxLig, idxLig + 1);
                    permutation = true;
                }
            }
            end = end + 1;
        }
    }

    /* Permet de comparer s1 à s2 */
    int comparer(String s1, String s2){
        int result;
            if(stringToInt(s1)==stringToInt(s2)){
                result=0;
            }else if(stringToInt(s1)>stringToInt(s2)){
                result=1;
            } else {
                result=-1;
            }
        return result;
    }

    /* Permet de permuter 2 lignes entre elles */
    void permuterLignes(String[][] csv, int ligneA, int ligneB) {
        String tmp;
        for (int idxCol = 0; idxCol < length(csv, 2); idxCol = idxCol + 1) {
            
            tmp = csv[ligneA][idxCol];
            csv[ligneA][idxCol] = csv[ligneB][idxCol];
            csv[ligneB][idxCol] = tmp;
        }
    }

    /* Permet d'afficher un tableau de toutes les parties avec l'identifiant et le nombre de points trier par odre décroissant */
    void classement(){
        jeuEnCours = true;
        while(jeuEnCours){
            println(lireTxt("classement.txt")+SL);
            String[][] tab = creerTableauCsv();
            trierSurColonne(tab);
            println(toString(tab));

            String quitter = changerString(readString());
            if(equals(quitter, "Quitter")){
                jeuEnCours = false;
                menuAccueil();
            }
        }
    }

// NIVEAU 1 :

    /* Permet de permutter une ligne d'une liste par une autre */
    void permuterLigne(String[] liste, int ligneA, int ligneB){
        String tmp = liste[ligneA];
        liste[ligneA] = liste[ligneB];
        liste[ligneB] = tmp;
    }

    /* Permet de trier la liste de toutes les planètes aléatoirement */
    String[] initialiserListe(){
        String[] ListePlanete = new String[]{"MERCURE","VENUS", "TERRE", "MARS", "JUPITER", "SATURNE", "URANUS", 
        "NEPTUNE", "MERCURE", "VENUS", "TERRE", "MARS", "JUPITER", "SATURNE", "URANUS", "NEPTUNE"};
        for (int IDX = length(ListePlanete)-1; IDX>0; IDX--){
            int aleaListe = (int)(random()*length(ListePlanete));
            permuterLigne(ListePlanete, IDX, aleaListe);
        }
        return ListePlanete;
    }  

    /* Permet d'initialier le tableau du niveau 1 avec les planètes aléatoirement */
    void initialiser(MemoryGame[][] memory){
        String[] ListePlanete = initialiserListe();
        int IDX_LISTE = 0;
        for(int l=0;l<length(memory,1); l++){
            for(int c=0;c<length(memory,2); c++){
                memory[l][c]=newPlanete(ListePlanete[IDX_LISTE]);
                IDX_LISTE+=1;
            }
        }
    }

    /* Permet de créer une planete avec la valeur mit en paramètre */
    MemoryGame newPlanete(String plan){
        MemoryGame p = new MemoryGame();
        p.decouverte=false;
        p.planete=plan;
        return p;
    }

    /* Permet de renvoyer si le jeu est fini */
    boolean testerDecouverte(MemoryGame[][] Memory){
        int IDX_L = 0;
        boolean result = true;
        while(IDX_L < length(Memory, 1) && result){
            int IDX_C = 0;
            while(IDX_C < length(Memory, 2) && result){
                if(Memory[IDX_L][IDX_C].decouverte==false){
                    result = false;
                }
                IDX_C += 1;
            }
            IDX_L += 1;
        }

        return !result;
    }   

    /* Renvoie la longueur du tableau maximale */
    String calculeTableau(int longueur, char car){
        String res = "";
        for (int IDX=0; IDX<longueur; IDX++){
            res+=car;
        }
        return res;
    } 
    
    /* determine le nombre de ligne */ 
    int maxLigne(MemoryGame[][] tab){
        int res = 0;
        for (int l=0; l<length(tab,1); l++){
            int n_res=0;
            for(int c=0; c<length(tab,2); c++){
                n_res+=length(tab[l][c].planete);
            }
            if(n_res>res){
                res=n_res;
            }
        }
        return res;
    }

    /* Permet de faire la première ligne du tableau de jeu */
    String premiereLigneTableau(){
        String res = "";
        for(int IDX=1; IDX<5; IDX++){
            res+= calculeTableau(4, '=') + " " + IDX + " " + calculeTableau(3, '=');
        }
        res += "=";
        return res;
    }
    
    /* Permet de renvoyer le tableau entier de jeu */
    String texte(MemoryGame[][] tab){
		String res = "";
        char[] ListeLettre = new char[]{'A', 'B', 'C', 'D'};
        res+= "   " + premiereLigneTableau() + SL; 
		for (int l=0;l<length(tab,1);l++){
            res += ListeLettre[l] + " ";
			for (int c=0;c<length(tab,2);c++){
                if(c==0){
                    res+= " | ";
                }
				res = res + texte(tab[l][c]) + " ";
                res+="| ";
			}	    
			res+= SL;
            res+= "   " + calculeTableau((maxLigne(tab)+15), '=') + SL;  
		}
		return res;
    }
    
    /* Retourne la chaîne de caractère prête à être affichée qui correspond à la planète passé en paramètre */
    String texte(MemoryGame p){
        if(p.decouverte==false){
			return "~~~~~~~";
		}else{
			return p.planete + genererEspace(p.planete, 7);
		}
    }

    /* Renvoie le nombre d'espace manquant pour faire une chaine de n caractères */
    String genererEspace(String chaine, int nb_generation){
        String res = "";
        for(int IDX_M = nb_generation; IDX_M>length(chaine); IDX_M--){
            res+=" ";
        }
        return res;
    }
    
    /* Permet de vérifier si 2 planètes découverte sont les mêmes */
    boolean sontLesMemes(MemoryGame[][] memory, int[] ValeursA, int[] ValeursB){
        boolean result = false;
        if(memory[ValeursA[0]][ValeursA[1]].planete==memory[ValeursB[0]][ValeursB[1]].planete){
            result = true;
        }
        return result;
    }

    /* Permet d'afficher une planète */
    void retournerCarte(MemoryGame[][] tab, int IDX_L, int IDX_C){
        tab[IDX_L][IDX_C].decouverte = true;
    }

    /* Permet d'enlever l'affichage d'une planète */
    void cacherCarte(MemoryGame[][] tab, int IDX_L, int IDX_C){
        tab[IDX_L][IDX_C].decouverte = false;
    }

    /* Permet de demander au joueur les 2 cartes à retourner */
    int[] demandeCarte(MemoryGame[][] tab){
        String x_String = "";
        int x = -1;
        int y = -1;
        boolean ValeurPossible = true;
        int[] result = new int[2];
        while(ValeurPossible){
            while(x<0 || x>3){
                println("Donne la ligne de la carte : A B C ou D");
                x_String = readString();
                if(length(x_String)==1){
                    if(equals(x_String,"A")){
                        x=0;
                    } else if(equals(x_String,"B")){
                        x=1;
                    } else if(equals(x_String,"C")){
                        x=2;
                    } else if(equals(x_String,"D")){
                        x=3;
                    }
                }
            }
            while(y<0 || y>3){
                println("Donne la colonne de la carte 1 2 3 ou 4");
                y = testerStringToInt(readString())-1;
            }
            if(x>=0 && x<=3 && y>=0 && y<=3){
                if(tab[x][y].decouverte==false){
                    ValeurPossible=false;
                } else {
                    x = -1;
                    y = -1;
                }
            }
        }
        result[0] = x;
        result[1] = y;
        return result;
    }

    /* Permet au programme de demander l'identifiant unique du joueur */
    String demanderIdentifiant(){
        String identifiant=",";
        while(verifierIdentifiant(identifiant) || chercherIdentifiant(identifiant) || length(identifiant)>11){
            if(chercherIdentifiant(identifiant)){
                println("Cet identifiant est déjà utilisé pour une partie merci d'en choisir un autre.");
            }
            println("Choisis un identifiant pour ta partie (sans virgule et moins de 11 caracèteres)");
            identifiant = changerString(readString());
        }
        return identifiant;
    }

    /* Permet de demander au joueur si il veut continuer à jouer après le niveau 1 */
    void finNiveau1(){
        String reponse = "";
        while(!equals(reponse,"oui") && !equals(reponse,"non")){
            println("Veux-tu continuer à jouer ? (oui / non)");
            reponse = readString();
            if(equals(reponse, "non")){
                ajouterUtilisateur(identifiantSauvegarde, 2, point_final);
                println("D'accord ta partie est enregistrée, à plus tard sur Planet Game !");
                menuAccueil();
                jeuEnCours=false;
            } else if (equals(reponse, "oui")){
                println("Allez continuons !");
                niveau2();
                jeuEnCours=false;
            }
        }
    }

    /* Permet d'afficher et de jouer au niveau 1 */
    void niveau1(){
        jeuEnCours = true;
        int points = 100;
        MemoryGame[][] Memory = new MemoryGame[4][4];
        initialiser(Memory);
        while(testerDecouverte(Memory) && jeuEnCours){
            clearScreen();
            println(lireTxt("Memory.txt")+SL);
            println(texte(Memory));
            println("Première carte");
            int[] ValeursA = demandeCarte(Memory);
            retournerCarte(Memory, ValeursA[0], ValeursA[1]);
            println(texte(Memory));
            println("Deuxième carte");
            int[] ValeursB = demandeCarte(Memory);
            retournerCarte(Memory, ValeursB[0], ValeursB[1]);
            println(texte(Memory));
            if(!(sontLesMemes(Memory, ValeursA, ValeursB))){
                cacherCarte(Memory, ValeursA[0], ValeursA[1]);
                cacherCarte(Memory, ValeursB[0], ValeursB[1]);
                println("Dommage retente ta chance !");
                if(points>15){
                    points-=2;
                }
            } else {
                println("Bien joué !");
            }
            delay(3000);
        }
        println("Bravo tu as fini le niveau avec "+ points+" points.");
        point_final+=points;
        finNiveau1();
    }

// SAUVEGARDE :

    /* Permet de voir si un identifiant est incorrect */
    boolean verifierIdentifiant(String identifiant){
        boolean result = false;
        if(equals(identifiant, "QUITTER")){
            result=true;
        }
        for(int IDX = 0; IDX<length(identifiant); IDX++){
            if(charAt(identifiant, IDX)==','){
                result = true;
            }
        }
        return result;
    }

    /* Permet d'ajouter un utilisateur dans la base de données */
    void ajouterUtilisateur(String identifiant, int niveauActuel, int points){
        String[][] tab = new String[rowCount(loadCSV(chemin))+1][columnCount(loadCSV(chemin))];

        for(int IDX_LIGNE=0; IDX_LIGNE<rowCount(loadCSV(chemin)); IDX_LIGNE++){
            for(int IDX_COL=0; IDX_COL<columnCount(loadCSV(chemin)); IDX_COL++){
                tab[IDX_LIGNE][IDX_COL]=getCell(loadCSV(chemin), IDX_LIGNE, IDX_COL);
            }
        }
        tab[length(tab)-1][0]= identifiant;
        tab[length(tab)-1][1]= "" + niveauActuel;
        tab[length(tab)-1][2]= "" + points;
        saveCSV(tab, chemin);
    }
    
    /* Permet de chercher un identifiant dans une base de données */
    boolean chercherIdentifiant(String identifiant){
        boolean result = false;
        for(int IDX_LIGNE=0; IDX_LIGNE<rowCount(loadCSV(chemin)); IDX_LIGNE++){
            if(equals(getCell(loadCSV(chemin), IDX_LIGNE, 0), identifiant)){
                result = true;
            }
        }
        return result;
    }

    /* Permet de savoir si la partie de l'utilisateur est finit ou non */
    int verifierNiveau(String identifiant){
        String result = "";
        for(int IDX_LIGNE=0; IDX_LIGNE<rowCount(loadCSV(chemin)); IDX_LIGNE++){
            if(equals(getCell(loadCSV(chemin), IDX_LIGNE, 0), identifiant)){
                result = getCell(loadCSV(chemin), IDX_LIGNE, 1);
            }
        }
        return stringToInt(result);
    }

    /* Permet de récuperer les points du joueur */
    int chercherPointJoueur(String identifiant){
        String result = "";
        for(int IDX_LIGNE=0; IDX_LIGNE<rowCount(loadCSV(chemin)); IDX_LIGNE++){
            if(equals(getCell(loadCSV(chemin), IDX_LIGNE, 0), identifiant)){
                result = getCell(loadCSV(chemin), IDX_LIGNE, 2);
            }
        }
        return stringToInt(result);
    }

    /* Renvoie la ligne correspondant au joueur dans la base de donnée */
    int chercherLigneJoueur(String identifiantPossible){
        int result = 0;
        for(int IDX_LIGNE=0; IDX_LIGNE<rowCount(loadCSV(chemin)); IDX_LIGNE++){
            if(equals(getCell(loadCSV(chemin), IDX_LIGNE, 0), identifiantPossible)){
                result = IDX_LIGNE;
            }
        }
        return result;
    }

    /* retire le joueur de la base de données */
    void enleverJoueur(String identifiantPossible){
        String[][] tab = new String[rowCount(loadCSV(chemin))-1][columnCount(loadCSV(chemin))];
        int idx = chercherLigneJoueur(identifiantPossible);
        
        for(int IDX_LIGNE=0; IDX_LIGNE<rowCount(loadCSV(chemin)); IDX_LIGNE++){
            for(int IDX_COL=0; IDX_COL<columnCount(loadCSV(chemin)); IDX_COL++){
                if(idx<IDX_LIGNE) {
                    tab[IDX_LIGNE-1][IDX_COL]=getCell(loadCSV(chemin), IDX_LIGNE, IDX_COL);
                } else {
                    tab[IDX_LIGNE][IDX_COL]=getCell(loadCSV(chemin), IDX_LIGNE, IDX_COL);
                }
            }
        }
        saveCSV(tab, chemin);
    }

// NIVEAU 2 :

    /* Permet de demander au joueur si il souhaite passer au prochain niveau ou non */
    void finNiveau2(){
        String reponse = "";
        while(!equals(reponse,"oui") && !equals(reponse,"non")){
            println("Veux-tu continuer à jouer ? (oui / non)");
            reponse = readString();
            if(equals(reponse, "non")){
                ajouterUtilisateur(identifiantSauvegarde, 3, point_final);
                println("D'accord ta partie est enregistrée, à plus tard sur Planet Game !");
                menuAccueil();
                jeuEnCours=false;
            } else if (equals(reponse, "oui")){
                println("Allez continuons !");
                niveau3();
                jeuEnCours=false;
            }
        }
    }

    /* Permet d'initialier le tableau du niveau 1 avec les planètes aléatoirement */
    void initialiserSysteme(PlanetPlace[] Systeme){
        String[] ListePlanete = new String[]{"Mercure","58","Venus","108","Terre","150","Mars","228","Jupiter","778","Saturne","1426","Uranus","2870","Neptune","4498"};
        int IDX_LISTE = 0;
        for(int indice=0;indice<length(Systeme); indice++){
            Systeme[indice]=newPlaneteSysteme(ListePlanete[IDX_LISTE], ListePlanete[IDX_LISTE+1]);
            IDX_LISTE+=2;
        }
    }

    /* Permet de créer une planete avec la valeur mit en paramètre */
    PlanetPlace newPlaneteSysteme(String plan, String dist){
        PlanetPlace p = new PlanetPlace();
        p.decouverte=false;
        p.planete=plan;
        p.distance=dist;
        return p;
    }

    /* Permet de generer la liste du jeu en chaine de caractères */
    String texteSysteme(PlanetPlace[] tab){
		String res = "|''| (_)    >    |``````    "; 
		for (int indice=0;indice<length(tab);indice++){
			res = res + " " +(indice+1) + " " + texteSysteme(tab[indice]) + " ";
		}
        res+="             |";
		return res;
    }
    
    /* Retourne la chaîne de caractère prête à être affichée qui correspond à la planète passé en paramètre */
    String texteSysteme(PlanetPlace p){
        if(p.decouverte==false){
			return "~~~~~~~";
		}else{
			return p.planete + genererEspace(p.planete, 7);
		}
    }

    /* Permet de demander ou veut jouer l'utilisateur */
    int demandeIndice(PlanetPlace[] tab){
        String x_String = "";
        int x = -1;
        boolean ValeurPossible = true;
        while(ValeurPossible){
            while(x<0 || x>9){
                println("Donne l'indice de la planete choisi entre 1 et 8");
                x = testerStringToInt(readString())-1;
            }
            if(x>=0 && x<=7){
                if(tab[x].decouverte==false){
                    ValeurPossible=false;
                } else {
                    x = -1;
                }
            }
        }
        return x;
    }

    /* Permet d'afficher une planete sur le jeu */
    void retournerPlanete(PlanetPlace[] tab, int IDX_P){
        tab[IDX_P].decouverte = true;
    }

    /* Permet de cacher une planete du jeu */
    void cacherPlanete(PlanetPlace[] tab, int IDX_P){
        tab[IDX_P].decouverte = false;
    }

    /* Permet de faire la fin du tableau du jeu qui s'affiche */
    String finTableau(){
        String res="|   \\    \\    / /                      *                                                                                         |"+SL;
        res+="|    `.   `--'.'                                                            *                        *                     *     |"+SL;
        res+="|   .' `-,,,-' `.                                                                                                                |"+SL;
        res+="| .'      :      `.      *                        *                                         *                                    |"+SL;
        res+="|                                                                                                    *                    *      |"+SL;
        res+="|                                 *                        *                 *                                                   |"+SL;
        res+="+================================================================================================================================+"+SL;
        return res;
    }

    /* Renvoie si le jeu est fini ou non */
    boolean testerJeuFini(PlanetPlace[] Systeme){
        int IDX_L = 0;
        boolean result = true;
        while(IDX_L < length(Systeme, 1) && result){
            if(Systeme[IDX_L].decouverte==false){
                result = false;
            }
            IDX_L += 1;
        }
        return !result;
    }   

    /* Converti les chiffres en miliars ou milion en fonction de la longueur de la distance */
    String convertir(String distance){
        String res="";
        if(length(distance)==4){
            res+=charAt(distance,0)+","+charAt(distance,0)+" miliards de";
        } else{
            res+=distance+" milions de";
        }
        return res;
    }

    /* Permet d'éviter une erreur de l'utilisateur pour avoir mit la bonne planete */
    String changerString(String chaine){
        String res="";
        for(int IDX=0; IDX<length(chaine); IDX++){
            if(IDX==0 && charAt(chaine,IDX)>=97 && charAt(chaine,IDX)<=122){
                res+=(char)(charAt(chaine,IDX)-32);
            }else if(charAt(chaine,IDX)>=65 && charAt(chaine,IDX)<=90 && IDX>0) {
                res+=(char)(charAt(chaine,IDX)+32);
            } else{
                res+=charAt(chaine,IDX);
            }
        }
        return res;
    }

    /* Permet de lancer le niveau 2 du jeu */
    void niveau2(){
        jeuEnCours = true;
        int points = 0;
        PlanetPlace[] Systeme = new PlanetPlace[8];
        initialiserSysteme(Systeme);
        int chancePossible = 3;
        while(testerJeuFini(Systeme) && jeuEnCours && chancePossible>0){
            clearScreen();
            println(lireTxt("PlanetPlace.txt"));
            println(texteSysteme(Systeme));
            println(finTableau());
            int indice_planete = demandeIndice(Systeme);
            println("Quel planete proposes tu ?");
            String planete = changerString(readString());
            if(equals(Systeme[indice_planete].planete, planete)){
                clearScreen();
                retournerPlanete(Systeme, indice_planete);
                println(lireTxt("PlanetPlace.txt"));
                println(texteSysteme(Systeme));
                println(finTableau());
                println("Bravo tu as trouvé ! Savez tu que "+Systeme[indice_planete].planete+" se situe à "+ convertir(Systeme[indice_planete].distance) +" kilomètres du soleil ?");
                points+=10;
                delay(5000);
            }else{
                chancePossible-=1;
                println("Dommage retente ta chance !");
                delay(2500);
            }
        }
        println(SL + "Bravo tu as fini le niveau avec "+ points+" points.");
        point_final+=points;
        finNiveau2();
    }


// NIVEAU 3 :

    /* Permet de vérifier si le joueur à trouver la bonne réponse */
    boolean verifierReponse(int IDX_COL, int IDX_LIGNE){
        boolean result = false;
        if(equals(getCell(loadCSV(cheminQuestion), IDX_LIGNE, IDX_COL), getCell(loadCSV(cheminQuestion), IDX_LIGNE, 5))){
            result=true;
        }
        return result;
    }

    /* Permet d'initialiser une liste d'élément de la question et les réponses */
    String[] initialierQuestion(int ligne){
        if(ligne==1){
            ligne+=1;
        }
        String[] result = new String[6];
        for(int IDX_COL=0; IDX_COL<6; IDX_COL++){
            result[IDX_COL] = getCell(loadCSV(cheminQuestion), ligne, IDX_COL);
        }
        return result;
    }

    /* Permet de faire l'affichage des possibles réponses pour l'utilisateur */
    String afficherReponse(int ligne){
        String result = "";
        char[] lettre = new char[]{'A','B','C','D'};
        for(int IDX_COL=1; IDX_COL<5; IDX_COL++){
            result += lettre[IDX_COL-1] + ": " + getCell(loadCSV(cheminQuestion), ligne, IDX_COL) + " ";
        }
        return result;
    }

    /* Permet de demander au joueur la réponse qu'il veut */
    int demandeReponse(){
        String x_String = "";
        int x = -1;
        boolean ValeurPossible = true;
        while(x<0 || x>4){
                println("Quel réponse proposes tu ? A B C ou D");
                x_String = readString();
                if(length(x_String)==1){
                    if(equals(x_String,"A")){
                        x=1;
                    } else if(equals(x_String,"B")){
                        x=2;
                    } else if(equals(x_String,"C")){
                        x=3;
                    } else if(equals(x_String,"D")){
                        x=4;
                    }
                }
        }
        return x;
    }

    /* Permet de mettre fin au jeu PlanetGame */
    void finNiveau3(){
        String reponse = "";
        println("Bravo ! Tu as fini le jeu PlanetGame avec "+point_final+" points. Merci d'avoir joué ta partie a été enregistrer.");
        ajouterUtilisateur(identifiantSauvegarde, 0, point_final);
        jeuEnCours=false;
        delay(3000);
        menuAccueil();
    }

    /* Permet de lancer le niveau 3 du jeu */
    void niveau3(){
        jeuEnCours = true;
        int points = 0;
        int chancePossible = 5;
        int numeroQuestion = 0;
        while(jeuEnCours && chancePossible>0){
            numeroQuestion+=1;
            clearScreen();
            println(lireTxt("PlanetQuestion.txt"));
            int ligne_question = (int) (random()*100);
            String[] question = initialierQuestion(ligne_question);
            println(SL + "Question "+numeroQuestion+": "+question[0] + SL + afficherReponse(ligne_question) + SL);
            int reponse = demandeReponse();
            if(verifierReponse(reponse, ligne_question)){
                println("Bravo tu as réussi ! Passons à la prochaine question !");
                points+=5;
            } else {
                println("Dommage la bonne réponse était " + question[5]);
                chancePossible-=1;
            }
            delay(3000);
        }
        println(SL + "Bravo tu as fini le niveau avec "+ points+" points." + SL);
        point_final+=points;
        finNiveau3();
    }

// FONCTIONS DE TEST :

    void testChangerString(){
        assertEquals("Mercure",changerString("mercure"));
        assertEquals("Mercure",changerString("MERCURE"));
        assertEquals("Mercure",changerString("MerCUre"));
    }

    void testPointJoueur(){
        assertEquals(1,chercherPointJoueur("testniveau"));
    }

    void testNiveau(){
        assertEquals(0,verifierNiveau("testniveau"));
    }

    void testChercherIdentifiant(){
        assertTrue(chercherIdentifiant("Giorgio2"));
        assertFalse(chercherIdentifiant("Giorgioooooooooooo"));
    }

    void testIdentifiant(){
        assertFalse(verifierIdentifiant("Giorgio"));
        assertTrue(verifierIdentifiant("Gio,rgio"));
        assertTrue(verifierIdentifiant("QUITTER"));
    }

    void testStringToInt(){
        assertEquals(1,testerStringToInt("1"));
        assertEquals(-1,testerStringToInt("12a"));
        assertEquals(-1,testerStringToInt(""));
    }

    void testComparer(){
        assertEquals(0,comparer("1","1"));
        assertEquals(1,comparer("2","1"));
        assertEquals(-1,comparer("1","2"));
    }

    void testInitialiserListe(){
        String[] ListePlanete = new String[]{"MERCURE","VENUS", "TERRE", "MARS", "JUPITER", "SATURNE", "URANUS", 
        "NEPTUNE", "MERCURE", "VENUS", "TERRE", "MARS", "JUPITER", "SATURNE", "URANUS", "NEPTUNE"};
        String[] nouvelle_Liste = initialiserListe();
        boolean result = false;
        if(ListePlanete!=nouvelle_Liste){
            result=true;
        }
        assertTrue(result);
    }
}
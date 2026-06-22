# Readify

Piattaforma web per l'acquisto di libri online, realizzata per l'esame di Ingegneria del Software presso l'Università degli Studi di Salerno.

Progetto di gruppo, sviluppato insieme a Paolo Visconti, Simone Sammartano e Gabriele De Luca.

Il sistema permette di sfogliare un catalogo di libri, gestire un carrello e completare un ordine, con un'area amministrativa per il controllo di catalogo, utenti e ordini. È organizzato secondo un'architettura a tre livelli con dipendenze in un'unica direzione.

## Tecnologie

- Java 21 con Jakarta Servlet API e JSTL per il backend
- JSP, HTML, CSS e JavaScript per il frontend
- MySQL per il database (`readify`), originariamente ospitato su Microsoft Azure
- Apache Tomcat 11 come server
- Maven per la build (packaging WAR)
- JUnit 5, Mockito 5 e JaCoCo per i test
- Gson per la serializzazione JSON

## Architettura

Il sistema segue un'architettura three-tier:

```
Controller -> Service -> DAO -> Database
```

- livello di presentazione: servlet, filtri e JSP (queste ultime non accessibili in modo diretto)
- livello di business: service e interfacce per i casi d'uso, con i pattern Facade e Observer
- livello dati: DAO per l'accesso a MySQL tramite JDBC

## Funzionalità

Utente non registrato:

- navigazione del catalogo con filtri e ricerca
- visualizzazione del dettaglio di un libro e delle recensioni

Utente registrato:

- registrazione, login e logout
- gestione del profilo, della password e degli indirizzi di spedizione
- carrello persistente e checkout con pagamento
- storico degli ordini con dettaglio e possibilità di annullamento
- aggiunta, modifica ed eliminazione delle recensioni

Amministratore:

- dashboard di controllo
- gestione del catalogo (inserimento, modifica ed eliminazione dei libri)
- gestione degli utenti
- gestione degli ordini e aggiornamento del loro stato
- moderazione delle recensioni

## Schema del database

Il database `readify` è composto dalle seguenti tabelle principali:

- `Utente`: dati anagrafici, ruolo (admin o registrato) e password in hash
- `Libro`: titolo, autore, ISBN, prezzo e disponibilità
- `Categoria` e `LibroCategoria`: categorie e relazione N:M con i libri
- `Indirizzo`: indirizzi di spedizione per utente
- `Carrello` e `DettaglioCarrello`: carrello persistente e relative righe
- `Ordine` e `Contiene`: testata e righe degli ordini, con il prezzo salvato al momento dell'acquisto
- `Recensione`: recensioni con voto e testo

Gli stati di un ordine seguono il percorso: in attesa, pagato, in elaborazione, spedito, consegnato, con le varianti annullato e rimborsato.

## Struttura del progetto

```
├── src/
│   ├── main/java/
│   │   ├── presentation/
│   │   │   ├── controller/       servlet per la gestione delle richieste HTTP
│   │   │   │   └── admin/        servlet dell'area amministratore
│   │   │   ├── filter/           filtri per autenticazione e carrello
│   │   │   └── util/             utilità e validazione dei form e dei pagamenti
│   │   ├── business/
│   │   │   ├── model/            entità di dominio (Libro, Utente, Ordine...)
│   │   │   ├── service/          interfacce e implementazioni dei servizi
│   │   │   └── ServiceFactory.java   factory per i service
│   │   └── data/
│   │       ├── dao/              accesso al database via JDBC
│   │       └── util/             gestione della connessione e hashing
│   ├── main/resources/
│   │   └── readify.sql           schema e dati iniziali
│   ├── main/webapp/
│   │   ├── WEB-INF/jsp/          viste JSP (utente e admin)
│   │   ├── css/                  fogli di stile
│   │   └── js/                   script client (filtri, carrello, validazione)
│   └── test/java/                test unitari e di integrazione
└── Documentazione/              elaborati di progetto (RAD, SDD, design pattern, test plan, presentazione)
```

## Come avviarlo

Prerequisiti: JDK 21+, Apache Tomcat 11+, Maven 3.8+.

1. Clonare il repository:

   ```bash
   git clone https://github.com/mariobranca16/Readify.git
   ```

2. Il database era originariamente ospitato su Microsoft Azure. Poiché l'istanza non è più attiva, è necessario ricrearlo in locale importando lo schema in MySQL:

   ```sql
   source src/main/resources/readify.sql
   ```

   e aggiornare di conseguenza i parametri di connessione (host, utente e password) in `src/main/java/data/util/DBManager.java`.

3. Generare il pacchetto e portarlo su Tomcat:

   ```bash
   mvn clean package
   ```

   Copiare il file `Readify.war` da `target/` nella cartella `webapps/` di Tomcat, oppure configurare il progetto dall'IDE.

4. Aprire l'applicazione su `http://localhost:8080/Readify`.

## Test

Il progetto include test unitari e di integrazione con JUnit 5 e Mockito, eseguibili con:

```bash
mvn test
```

Coprono i flussi principali: login e registrazione, carrello e checkout, aggiornamento dei dati utente, ricerca nel catalogo e validazione di indirizzo, pagamento e form di registrazione.

## Sicurezza

- password cifrate con hash nel database
- validazione degli input sia lato client sia lato server
- filtri servlet per il controllo degli accessi
- JSP collocate in `WEB-INF`, non raggiungibili in modo diretto

## Documentazione

La cartella `Documentazione/` raccoglie gli elaborati prodotti durante il corso: l'analisi dei requisiti (RAD), il documento di system design (SDD), l'analisi dei design pattern adottati, il piano di test (TP) con i relativi test case scenario (TCS) e test incident report (TIR), e la presentazione finale del progetto.

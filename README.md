# Readify 📚

> Piattaforma web per l'acquisto di libri online, sviluppata nell'ambito del corso di **Ingegneria del Software** presso l'**Università degli Studi di Salerno**.

> Progetto realizzato da
> - **Mario Branca**
> - **Paolo Visconti**
> - **Simone Sammartano**
> - **Gabriele De Luca**

---

## Stack tecnologico

| Layer        | Tecnologie                                          |
|--------------|-----------------------------------------------------|
| Backend      | Java 21, Jakarta Servlet API 6.1, JSTL 3.0          |
| Frontend     | JSP, HTML/CSS, JavaScript                           |
| Database     | MySQL (`readify`) — hostato su Microsoft Azure      |
| Server       | Apache Tomcat 11                                    |
| Build        | Maven (packaging WAR)                               |
| Test         | JUnit 5, Mockito 5, JaCoCo                          |
| Serializzaz. | Gson 2.10                                           |

---

## Architettura

Il sistema adotta un'architettura **three-tier** con dipendenze unidirezionali:

```
Controller → Service → DAO → Database
```

- **Presentation layer** — Servlet, Filter e JSP (non accessibili direttamente)
- **Business layer** — Service e interfacce per i casi d'uso, con pattern Facade e Observer
- **Data layer** — DAO per l'accesso a MySQL tramite JDBC

---

## Funzionalità

### Utente non registrato
- Navigazione del catalogo con filtri e ricerca
- Visualizzazione dettaglio libro e recensioni

### Utente registrato
- Registrazione, login e logout
- Gestione profilo, password e indirizzi di spedizione
- Carrello persistente e checkout con pagamento
- Storico ordini con dettaglio e possibilità di annullamento
- Recensioni sui libri (aggiunta, modifica, eliminazione)

### Amministratore
- Dashboard con pannello di controllo
- Gestione catalogo libri (inserimento, modifica, eliminazione)
- Gestione utenti (visualizzazione, modifica, eliminazione)
- Gestione ordini (visualizzazione e aggiornamento stato)
- Moderazione recensioni

---

## Schema del database

Il database `readify` include le seguenti tabelle principali:

| Tabella             | Descrizione                                         |
|---------------------|-----------------------------------------------------|
| `Utente`            | Dati anagrafici, ruolo (`admin`/`registrato`), hash |
| `Libro`             | Titolo, autore, ISBN, prezzo, disponibilità         |
| `Categoria`         | Categorie con icona                                 |
| `LibroCategoria`    | Relazione N:M libro–categoria                       |
| `Indirizzo`         | Indirizzi di spedizione per utente                  |
| `Carrello`          | Carrello persistente per utente                     |
| `DettaglioCarrello` | Righe carrello con prezzo unitario                  |
| `Ordine`            | Testata ordine con stato e totale                   |
| `Contiene`          | Righe ordine con snapshot prezzo al momento         |
| `Recensione`        | Recensioni con voto e testo                         |

Stati ordine: `in_attesa` → `pagato` → `in_elaborazione` → `spedito` → `consegnato` / `annullato` / `rimborsato`

---

## Installazione

### Prerequisiti

- JDK 21+
- Apache Tomcat 11+
- Maven 3.8+

### 1. Clona il repository

```bash
git clone https://github.com/mariobranca16/Readify.git
```

### 2. Database

Il database è hostato su **Microsoft Azure** e non richiede configurazione locale.

Se si desidera usare un'istanza MySQL locale, importare lo schema:

```sql
source src/main/resources/readify.sql
```

Quindi aggiornare le credenziali in `src/main/java/data/util/DBManager.java`:

```java
private static final String HOST     = "MY_HOST";      // es. localhost
private static final String DB       = "readify";
private static final String USER     = "MY_USERNAME";  // es. root
private static final String PASSWORD = "MY_PASSWORD";
```

> I valori `MY_HOST`, `MY_USERNAME` e `MY_PASSWORD` sono segnaposto e vanno sostituiti con le credenziali della propria istanza MySQL.

### 3. Build e deploy

```bash
mvn clean package
```

Copia il file `Readify.war` generato in `target/` nella cartella `webapps/` di Tomcat, oppure configura il progetto in IntelliJ IDEA:

- `Run > Edit Configurations... > + > Tomcat Server (Local)`
- Nella sezione **Deployment** aggiungere l'artefatto WAR/Exploded

### 4. Avvia l'applicazione

```
http://localhost:8080/Readify
```

---

## Test

Il progetto include test unitari e di integrazione con JUnit 5 e Mockito:

```bash
mvn test
```

| Classe di test                     | Copertura                           |
|------------------------------------|-------------------------------------|
| `LoginControllerTest`              | Autenticazione utente               |
| `RegistrazioneControllerTest`      | Validazione registrazione           |
| `AcquistoControllerTest`           | Flusso carrello e checkout          |
| `ModificaDatiUtenteControllerTest` | Aggiornamento profilo               |
| `RicercaPerNomeTest`               | Ricerca nel catalogo                |
| `ValidaIndirizzoTest`              | Validazione indirizzo spedizione    |
| `ValidaPagamentoTest`              | Validazione dati pagamento          |
| `ValidaRegistrazioneTest`          | Validazione form registrazione      |

---

## Struttura del progetto

```
src/
├── main/java/
│   ├── presentation/
│   │   ├── controller/       # Servlet — gestione richieste HTTP
│   │   │   └── admin/        # Servlet area amministratore
│   │   ├── filter/           # Filtri (autenticazione, carrello)
│   │   └── util/             # ServletUtils, ValidatoreForm, ValidatorePagamento
│   ├── business/
│   │   ├── model/            # Entità di dominio (Libro, Utente, Ordine…)
│   │   ├── service/          # Interfacce e implementazioni dei servizi
│   │   │   └── catalog/observer/  # Pattern Observer (consistenza carrello)
│   │   └── ServiceFactory.java    # Factory per i service
│   └── data/
│       ├── dao/              # Accesso al database via JDBC
│       └── util/             # DBManager, HashUtil
├── main/resources/
│   └── readify.sql           # Schema e dati iniziali
├── main/webapp/
│   ├── WEB-INF/jsp/          # Viste JSP (utente e admin)
│   ├── css/                  # Fogli di stile per pagina
│   └── js/                   # Script client (filtri, carrello, validazione)
└── test/java/                # Test unitari e di integrazione
```

---

## Sicurezza

- Password cifrate con hash nel database (`HashUtil`)
- Validazione lato client (`form-validation.js`) e lato server (`ValidatoreForm`, `ValidatorePagamento`)
- Filtri servlet per controllo accessi (`AuthenticationFilter`, `CarrelloFilter`)
- JSP non direttamente accessibili (collocate in `WEB-INF`)
- Connessione al database Azure con SSL obbligatorio (`sslMode=REQUIRED`)

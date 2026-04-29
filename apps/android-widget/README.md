# CodexBar Android Widget

Questa applicazione permette di visualizzare gli usage di CodexBar (PC) direttamente sul tuo smartphone Android tramite un widget minimale.

## Configurazione

1.  **PC (Windows)**:
    *   Assicurati che `settings.json` in `%LOCALAPPDATA%\CodexBar\` contenga il campo `"google_script_url"` con l'URL del tuo Google Apps Script.
2.  **Middleware**:
    *   Usa il Google Apps Script fornito per ricevere i dati dal PC via POST e servirli al telefono via GET.
3.  **Android**:
    *   Installa l'app tramite Android Studio.
    *   Apri l'app per configurare le soglie di allerta (Giallo/Rosso).
    *   Aggiungi il widget "CodexBar" alla tua Home Screen.

## Funzionalità
*   **Doppia Barra**: Visualizzazione simultanea dei limiti di sessione (5h) e settimanali (W).
*   **Colori Dinamici**: Le barre cambiano colore in base all'utilizzo.
*   **Auto-Sync**: Il widget si aggiorna ogni volta che il PC rileva un cambiamento negli usage.
*   **Refresh Manuale**: Pulsante in-app per forzare la sincronizzazione.

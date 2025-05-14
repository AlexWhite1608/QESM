# Valutazione Quantitativa di Modelli Stocastici – Simulazione di Fog Offloading

**Autore**: Alessandro Bianco  
**Data**: Febbraio 2025

**Esame**: Quantitative Evaluation of Stochastic Models

## Descrizione del progetto
Questo progetto sviluppa una simulazione dinamica per l’**offloading** di task da un insieme di client a nodi fog, utilizzando una variante stabile dell’algoritmo di Gale–Shapley.

- I **client** valutano i nodi in base a ritardo di coda, tempo di esecuzione e distanza euclidea.
- I **nodi fog** preferiscono client con tempi di attesa più elevati e distanza ridotta.
- La simulazione gestisce arrivi e partenze dinamiche dei client, riequilibrando gli abbinamenti tramite swap per mantenere la stabilità e ottimizzare metriche come il tempo di attesa medio e il numero di swap effettuati.
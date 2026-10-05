# Graph Report - PDV-Elmix  (2026-10-05)

## Corpus Check
- 34 files · ~17,002 words
- Verdict: corpus is large enough that graph structure adds value.

## Summary
- 539 nodes · 1350 edges · 23 communities (19 shown, 4 thin omitted)
- Extraction: 74% EXTRACTED · 26% INFERRED · 0% AMBIGUOUS · INFERRED: 345 edges (avg confidence: 0.8)
- Token cost: 0 input · 0 output

## Community Hubs (Navigation)
- [[_COMMUNITY_Community 0|Community 0]]
- [[_COMMUNITY_Community 1|Community 1]]
- [[_COMMUNITY_Community 2|Community 2]]
- [[_COMMUNITY_Community 3|Community 3]]
- [[_COMMUNITY_Community 4|Community 4]]
- [[_COMMUNITY_Community 5|Community 5]]
- [[_COMMUNITY_Community 6|Community 6]]
- [[_COMMUNITY_Community 7|Community 7]]
- [[_COMMUNITY_Community 8|Community 8]]
- [[_COMMUNITY_Community 9|Community 9]]
- [[_COMMUNITY_Community 10|Community 10]]
- [[_COMMUNITY_Community 11|Community 11]]
- [[_COMMUNITY_Community 12|Community 12]]
- [[_COMMUNITY_Community 13|Community 13]]
- [[_COMMUNITY_Community 14|Community 14]]
- [[_COMMUNITY_Community 15|Community 15]]
- [[_COMMUNITY_Community 21|Community 21]]
- [[_COMMUNITY_Community 22|Community 22]]

## God Nodes (most connected - your core abstractions)
1. `ElmixApp` - 44 edges
2. `TelaFinanceiroView` - 38 edges
3. `TelaPDVView` - 29 edges
4. `FinanceiroController` - 28 edges
5. `VendaController` - 22 edges
6. `Configuracao` - 21 edges
7. `TelaInicialView` - 21 edges
8. `MovimentacaoCaixa` - 19 edges
9. `Venda` - 18 edges
10. `TelaConfigView` - 18 edges

## Surprising Connections (you probably didn't know these)
- `BackupCompleto` --implements--> `Serializable`  [EXTRACTED]
  demo4/src/main/java/pdv/demo4/model/BackupCompleto.java →   _Bridges community 7 → community 10_
- `Configuracao` --implements--> `Serializable`  [EXTRACTED]
  demo4/src/main/java/pdv/demo4/model/Configuracao.java →   _Bridges community 10 → community 2_
- `Empresa` --implements--> `Serializable`  [EXTRACTED]
  demo4/src/main/java/pdv/demo4/model/Empresa.java →   _Bridges community 10 → community 12_
- `MovimentacaoCaixa` --implements--> `Serializable`  [EXTRACTED]
  demo4/src/main/java/pdv/demo4/model/MovimentacaoCaixa.java →   _Bridges community 10 → community 8_
- `Venda` --implements--> `Serializable`  [EXTRACTED]
  demo4/src/main/java/pdv/demo4/model/Venda.java →   _Bridges community 10 → community 4_

## Import Cycles
- None detected.

## Communities (23 total, 4 thin omitted)

### Community 0 - "Community 0"
Cohesion: 0.10
Nodes (18): Application, ElmixApp, Override, String, Venda, Object, Button, List (+10 more)

### Community 1 - "Community 1"
Cohesion: 0.07
Nodes (20): ComboBox, BigDecimal, BorderPane, Button, Configuracao, Consumer, DatePicker, Empresa (+12 more)

### Community 2 - "Community 2"
Cohesion: 0.07
Nodes (16): CheckBox, String, BorderPane, Button, Configuracao, HBox, String, TextField (+8 more)

### Community 3 - "Community 3"
Cohesion: 0.14
Nodes (11): FinanceiroController, BigDecimal, DadosFinanceiros, Empresa, List, LocalDate, LocalDateTime, Map (+3 more)

### Community 4 - "Community 4"
Cohesion: 0.08
Nodes (14): ItemVenda, List, LocalDateTime, Override, String, BorderPane, Button, Configuracao (+6 more)

### Community 5 - "Community 5"
Cohesion: 0.16
Nodes (8): BorderPane, Button, Configuracao, HBox, String, TextArea, VBox, TelaMoneyView

### Community 6 - "Community 6"
Cohesion: 0.11
Nodes (10): BorderPane, Button, Configuracao, Label, StackPane, String, VBox, EventHandler (+2 more)

### Community 7 - "Community 7"
Cohesion: 0.10
Nodes (12): Configuracao, DadosFinanceiros, List, Venda, BorderPane, Button, Configuracao, DatePicker (+4 more)

### Community 8 - "Community 8"
Cohesion: 0.15
Nodes (8): BigDecimal, Empresa, LocalDateTime, Override, String, MovimentacaoCaixa, OrigemMovimentacao, TipoMovimentacao

### Community 9 - "Community 9"
Cohesion: 0.16
Nodes (8): ConfiguracaoController, Configuracao, List, Object, String, T, SuppressWarnings, PersistenciaUtil

### Community 10 - "Community 10"
Cohesion: 0.14
Nodes (8): Empresa, List, MovimentacaoCaixa, Override, String, DadosFinanceiros, ItemVenda, Serializable

### Community 11 - "Community 11"
Cohesion: 0.18
Nodes (5): VendaController, List, LocalDate, String, Venda

### Community 12 - "Community 12"
Cohesion: 0.09
Nodes (12): LocalDate, Override, String, BorderPane, Button, Configuracao, Empresa, List (+4 more)

### Community 13 - "Community 13"
Cohesion: 0.10
Nodes (12): Configuracao, Consumer, HBox, ItemVenda, Label, ListView, StackPane, String (+4 more)

## Knowledge Gaps
- **39 isolated node(s):** `recordToolUse.sh script`, `$schema`, `plugin`, `@opencode-ai/plugin`, `Override` (+34 more)
  These have ≤1 connection - possible missing edges or undocumented components.
- **4 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `ElmixApp` connect `Community 0` to `Community 2`, `Community 3`, `Community 4`, `Community 6`, `Community 7`, `Community 11`, `Community 12`?**
  _High betweenness centrality (0.153) - this node is a cross-community bridge._
- **Why does `Configuracao` connect `Community 2` to `Community 1`, `Community 10`, `Community 5`, `Community 6`?**
  _High betweenness centrality (0.075) - this node is a cross-community bridge._
- **What connects `recordToolUse.sh script`, `$schema`, `plugin` to the rest of the system?**
  _39 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `Community 0` be split into smaller, more focused modules?**
  _Cohesion score 0.10230179028132992 - nodes in this community are weakly interconnected._
- **Should `Community 1` be split into smaller, more focused modules?**
  _Cohesion score 0.06779661016949153 - nodes in this community are weakly interconnected._
- **Should `Community 2` be split into smaller, more focused modules?**
  _Cohesion score 0.06778476589797344 - nodes in this community are weakly interconnected._
- **Should `Community 3` be split into smaller, more focused modules?**
  _Cohesion score 0.14285714285714285 - nodes in this community are weakly interconnected._
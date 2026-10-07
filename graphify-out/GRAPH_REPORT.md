# Graph Report - PDV-Elmix  (2026-10-07)

## Corpus Check
- 43 files · ~27,092 words
- Verdict: corpus is large enough that graph structure adds value.

## Summary
- 683 nodes · 1586 edges · 29 communities (25 shown, 4 thin omitted)
- Extraction: 77% EXTRACTED · 23% INFERRED · 0% AMBIGUOUS · INFERRED: 369 edges (avg confidence: 0.8)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `cdcffddc`
- Run `git rev-parse HEAD` and compare to check if the graph is stale.
- Run `graphify update .` after code changes (no API cost).

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
- [[_COMMUNITY_Community 16|Community 16]]
- [[_COMMUNITY_Community 17|Community 17]]
- [[_COMMUNITY_Community 18|Community 18]]
- [[_COMMUNITY_Community 19|Community 19]]
- [[_COMMUNITY_Community 26|Community 26]]
- [[_COMMUNITY_Community 27|Community 27]]

## God Nodes (most connected - your core abstractions)
1. `ElmixApp` - 44 edges
2. `TelaFinanceiroView` - 41 edges
3. `FinanceiroController` - 31 edges
4. `TelaPDVView` - 29 edges
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
- `EstadoFatura` --implements--> `Serializable`  [EXTRACTED]
  demo4/src/main/java/pdv/demo4/model/EstadoFatura.java →   _Bridges community 10 → community 26_
- `FaturaFinanceira` --implements--> `Serializable`  [EXTRACTED]
  demo4/src/main/java/pdv/demo4/model/FaturaFinanceira.java →   _Bridges community 10 → community 13_

## Import Cycles
- None detected.

## Communities (29 total, 4 thin omitted)

### Community 0 - "Community 0"
Cohesion: 0.08
Nodes (21): Application, ElmixApp, Venda, Override, String, Venda, ItemVenda, List (+13 more)

### Community 1 - "Community 1"
Cohesion: 0.07
Nodes (22): ComboBox, BigDecimal, BorderPane, Button, Configuracao, Consumer, DatePicker, Empresa (+14 more)

### Community 2 - "Community 2"
Cohesion: 0.06
Nodes (19): CheckBox, String, BorderPane, Button, Configuracao, HBox, String, TextField (+11 more)

### Community 3 - "Community 3"
Cohesion: 0.14
Nodes (12): FinanceiroController, BigDecimal, DadosFinanceiros, Empresa, FaturaFinanceira, List, LocalDate, LocalDateTime (+4 more)

### Community 4 - "Community 4"
Cohesion: 0.17
Nodes (8): BorderPane, Button, Configuracao, List, ListView, Venda, Optional, TelaHistoricoView

### Community 5 - "Community 5"
Cohesion: 0.10
Nodes (13): Button, Configuracao, Consumer, HBox, ItemVenda, Label, ListView, StackPane (+5 more)

### Community 6 - "Community 6"
Cohesion: 0.09
Nodes (13): BorderPane, Button, Configuracao, Label, String, BorderPane, Button, Configuracao (+5 more)

### Community 7 - "Community 7"
Cohesion: 0.06
Nodes (20): ConfiguracaoController, VendaController, Configuracao, List, LocalDate, String, Venda, Configuracao (+12 more)

### Community 8 - "Community 8"
Cohesion: 0.15
Nodes (8): BigDecimal, Empresa, LocalDateTime, Override, String, MovimentacaoCaixa, OrigemMovimentacao, TipoMovimentacao

### Community 9 - "Community 9"
Cohesion: 0.14
Nodes (7): BorderPane, Button, Configuracao, DatePicker, Label, TextArea, TelaBackupView

### Community 10 - "Community 10"
Cohesion: 0.14
Nodes (9): Empresa, FaturaFinanceira, List, MovimentacaoCaixa, Override, String, DadosFinanceiros, ItemVenda (+1 more)

### Community 11 - "Community 11"
Cohesion: 0.07
Nodes (26): 10. Atalho `/` — Venda em paralelo, 11. Regras importantes para o PDV paralelo — CONCLUÍDO, 12. Compatibilidade com funcionalidades existentes, 13. Banco de dados, 14. Interface, 15. Checklist final, 1. Correções gerais, 2. Histórico de vendas (+18 more)

### Community 12 - "Community 12"
Cohesion: 0.09
Nodes (12): LocalDate, Override, String, BorderPane, Button, Configuracao, Empresa, List (+4 more)

### Community 13 - "Community 13"
Cohesion: 0.13
Nodes (11): BigDecimal, Empresa, EstadoFatura, LocalDate, LocalDateTime, String, FaturaFinanceira, LocalDate (+3 more)

### Community 16 - "Community 16"
Cohesion: 0.14
Nodes (13): 1. Tela Inicial, 2. Tela PDV (Ponto de Venda), 3. Tela Conta, 4. Tela Configurações, 5. Tela Histórico, 6. Tela Backup, Como os Dados são Salvos, Como Rodar (+5 more)

### Community 17 - "Community 17"
Cohesion: 0.25
Nodes (7): 1. Modal de Venda Paralela, 2. Tela de Empresas, Ajustes — Venda Paralela e Empresas, Funcionamento, Identidade visual, Importante, Regras:

### Community 18 - "Community 18"
Cohesion: 0.50
Nodes (3): Executar, PDV, Requisitos

### Community 26 - "Community 26"
Cohesion: 0.06
Nodes (24): FaturaFinanceira, LocalDate, LocalDateTime, String, FaturaFinanceira, LocalDate, LocalDateTime, Override (+16 more)

## Knowledge Gaps
- **85 isolated node(s):** `recordToolUse.sh script`, `Override`, `Parent`, `EventHandler`, `TelaEmpresasView` (+80 more)
  These have ≤1 connection - possible missing edges or undocumented components.
- **4 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `ElmixApp` connect `Community 0` to `Community 2`, `Community 3`, `Community 6`, `Community 7`, `Community 12`?**
  _High betweenness centrality (0.105) - this node is a cross-community bridge._
- **Why does `TelaFinanceiroView` connect `Community 1` to `Community 3`?**
  _High betweenness centrality (0.082) - this node is a cross-community bridge._
- **Why does `Configuracao` connect `Community 2` to `Community 10`, `Community 6`?**
  _High betweenness centrality (0.078) - this node is a cross-community bridge._
- **What connects `recordToolUse.sh script`, `Override`, `Parent` to the rest of the system?**
  _85 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `Community 0` be split into smaller, more focused modules?**
  _Cohesion score 0.07675070028011205 - nodes in this community are weakly interconnected._
- **Should `Community 1` be split into smaller, more focused modules?**
  _Cohesion score 0.0671602326811211 - nodes in this community are weakly interconnected._
- **Should `Community 2` be split into smaller, more focused modules?**
  _Cohesion score 0.057971014492753624 - nodes in this community are weakly interconnected._
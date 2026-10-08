# Graph Report - PDV-Elmix  (2026-10-07)

## Corpus Check
- 56 files · ~30,036 words
- Verdict: corpus is large enough that graph structure adds value.

## Summary
- 787 nodes · 1789 edges · 39 communities (34 shown, 5 thin omitted)
- Extraction: 77% EXTRACTED · 23% INFERRED · 0% AMBIGUOUS · INFERRED: 420 edges (avg confidence: 0.8)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `76a6638e`
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
- [[_COMMUNITY_Community 29|Community 29]]
- [[_COMMUNITY_Community 30|Community 30]]
- [[_COMMUNITY_Community 31|Community 31]]
- [[_COMMUNITY_Community 32|Community 32]]
- [[_COMMUNITY_Community 34|Community 34]]
- [[_COMMUNITY_Community 35|Community 35]]
- [[_COMMUNITY_Community 36|Community 36]]

## God Nodes (most connected - your core abstractions)
1. `ElmixApp` - 49 edges
2. `TelaFinanceiroView` - 42 edges
3. `FinanceiroController` - 33 edges
4. `TelaPDVView` - 29 edges
5. `Configuracao` - 23 edges
6. `VendaController` - 22 edges
7. `TelaInicialView` - 21 edges
8. `MovimentacaoCaixa` - 19 edges
9. `TelaConfigView` - 19 edges
10. `Venda` - 18 edges

## Surprising Connections (you probably didn't know these)
- `BackupCompleto` --implements--> `Serializable`  [EXTRACTED]
  demo4/src/main/java/pdv/demo4/model/BackupCompleto.java →   _Bridges community 9 → community 10_
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

## Communities (39 total, 5 thin omitted)

### Community 0 - "Community 0"
Cohesion: 0.05
Nodes (31): Application, ElmixApp, Override, Venda, Runnable, Stage, String, Venda (+23 more)

### Community 1 - "Community 1"
Cohesion: 0.07
Nodes (22): ComboBox, BigDecimal, BorderPane, Button, Configuracao, Consumer, DatePicker, Empresa (+14 more)

### Community 2 - "Community 2"
Cohesion: 0.07
Nodes (13): CheckBox, String, BorderPane, Button, Configuracao, HBox, String, TextField (+5 more)

### Community 3 - "Community 3"
Cohesion: 0.12
Nodes (13): FinanceiroController, BigDecimal, DadosFinanceiros, Empresa, FaturaFinanceira, List, LocalDate, LocalDateTime (+5 more)

### Community 4 - "Community 4"
Cohesion: 0.18
Nodes (7): BorderPane, Button, Configuracao, List, ListView, Venda, TelaHistoricoView

### Community 5 - "Community 5"
Cohesion: 0.12
Nodes (11): EventoFatura, FaturaFinanceira, TipoEventoFatura, Override, Consumer, EventoFatura, Stage, String (+3 more)

### Community 6 - "Community 6"
Cohesion: 0.10
Nodes (11): BorderPane, Button, Configuracao, Label, StackPane, VBox, Configuracao, HBox (+3 more)

### Community 7 - "Community 7"
Cohesion: 0.12
Nodes (11): VendaController, List, LocalDate, String, Venda, List, Object, String (+3 more)

### Community 8 - "Community 8"
Cohesion: 0.15
Nodes (8): BigDecimal, Empresa, LocalDateTime, Override, String, MovimentacaoCaixa, OrigemMovimentacao, TipoMovimentacao

### Community 9 - "Community 9"
Cohesion: 0.08
Nodes (14): ConfiguracaoController, Configuracao, Configuracao, DadosFinanceiros, List, Venda, BorderPane, Button (+6 more)

### Community 10 - "Community 10"
Cohesion: 0.11
Nodes (11): Empresa, FaturaFinanceira, List, MovimentacaoCaixa, Override, String, List, LocalDateTime (+3 more)

### Community 11 - "Community 11"
Cohesion: 0.07
Nodes (26): 10. Atalho `/` — Venda em paralelo, 11. Regras importantes para o PDV paralelo — CONCLUÍDO, 12. Compatibilidade com funcionalidades existentes, 13. Banco de dados, 14. Interface, 15. Checklist final, 1. Correções gerais, 2. Histórico de vendas (+18 more)

### Community 12 - "Community 12"
Cohesion: 0.09
Nodes (12): LocalDate, Override, String, BorderPane, Button, Configuracao, Empresa, List (+4 more)

### Community 13 - "Community 13"
Cohesion: 0.12
Nodes (12): BigDecimal, Empresa, EstadoFatura, LocalDate, LocalDateTime, String, FaturaFinanceira, LocalDate (+4 more)

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

### Community 29 - "Community 29"
Cohesion: 0.16
Nodes (8): BorderPane, Button, Configuracao, HBox, String, TextArea, VBox, TelaMoneyView

### Community 30 - "Community 30"
Cohesion: 0.20
Nodes (9): PoliticaAlertasFatura, PoliticaAlertasFaturaTest, EventoFatura, FaturaFinanceira, LocalDate, Optional, FaturaFinanceira, LocalDate (+1 more)

### Community 31 - "Community 31"
Cohesion: 0.22
Nodes (8): AssinaturaEventoFatura, GerenciadorEventosFatura, GerenciadorEventosFaturaTest, EventoFatura, TipoEventoFatura, FaturaFinanceira, Test, ObservadorFatura

### Community 32 - "Community 32"
Cohesion: 0.21
Nodes (6): BorderPane, Button, Configuracao, Label, String, TelaContaView

### Community 34 - "Community 34"
Cohesion: 0.22
Nodes (8): Benefícios, Benefícios, Classes afetadas pelo Observer, Classes afetadas pelo State, Observer — mudanças e alertas de fatura, Padrões de projeto no PDV Elmix, Regras dos lembretes, State — ciclo de vida da fatura

### Community 35 - "Community 35"
Cohesion: 0.40
Nodes (3): AutoCloseable, AssinaturaEventoFatura, Runnable

## Knowledge Gaps
- **97 isolated node(s):** `recordToolUse.sh script`, `Parent`, `EventHandler`, `TelaEmpresasView`, `TelaFinanceiroView` (+92 more)
  These have ≤1 connection - possible missing edges or undocumented components.
- **5 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `ElmixApp` connect `Community 0` to `Community 2`, `Community 3`, `Community 5`, `Community 6`, `Community 7`, `Community 9`, `Community 12`?**
  _High betweenness centrality (0.131) - this node is a cross-community bridge._
- **Why does `TelaFinanceiroView` connect `Community 1` to `Community 2`, `Community 3`?**
  _High betweenness centrality (0.070) - this node is a cross-community bridge._
- **Why does `Configuracao` connect `Community 2` to `Community 10`, `Community 6`?**
  _High betweenness centrality (0.067) - this node is a cross-community bridge._
- **What connects `recordToolUse.sh script`, `Parent`, `EventHandler` to the rest of the system?**
  _97 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `Community 0` be split into smaller, more focused modules?**
  _Cohesion score 0.054993678887484194 - nodes in this community are weakly interconnected._
- **Should `Community 1` be split into smaller, more focused modules?**
  _Cohesion score 0.06557377049180328 - nodes in this community are weakly interconnected._
- **Should `Community 2` be split into smaller, more focused modules?**
  _Cohesion score 0.07086197778952935 - nodes in this community are weakly interconnected._
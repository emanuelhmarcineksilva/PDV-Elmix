# Padrões de projeto no PDV Elmix

Este documento resume como **State** e **Observer** são aplicados ao fluxo de
faturas, quais responsabilidades cada padrão cobre e quais classes participam.

## State — ciclo de vida da fatura

`FaturaFinanceira` é o contexto: guarda os dados da fatura e encaminha as
operações de estado ao objeto `EstadoFatura` atual. Os estados concretos
implementam o comportamento próprio de cada fase:

- `EstadoFaturaAberta`: permite quitar e muda para atrasada depois do vencimento.
- `EstadoFaturaAtrasada`: continua permitindo a quitação e não volta para aberta.
- `EstadoFaturaQuitada`: é final e impede uma segunda quitação.
 
### Benefícios

- Regras de transição ficam junto dos estados, em vez de se espalharem por
  `ElmixApp`, telas e controladores.
- Cada estado expressa claramente quais operações são permitidas.
- A fatura continua sendo a autoridade sobre o próprio ciclo de vida.
- As regras podem ser testadas sem abrir a interface.
- Faturas serializadas antes da introdução do State recuperam um estado coerente
  ao serem desserializadas.

### Classes afetadas pelo State

- `FaturaFinanceira`: contexto; delega consultas e pedidos de quitação.
- `EstadoFatura`: contrato abstrato dos estados.
- `EstadoFaturaAberta`, `EstadoFaturaAtrasada` e `EstadoFaturaQuitada`:
  comportamentos e transições concretas.
- `FinanceiroController`: solicita a quitação sem duplicar as regras de estado.
- `TelaFinanceiroView`: apresenta o estado fornecido pela fatura e habilita ou
  desabilita o botão de quitação.
- `FaturaFinanceiraTest`: verifica estados, transições e persistência.

## Observer — mudanças e alertas de fatura

`FinanceiroController` publica eventos depois de cadastrar ou quitar faturas.
`GerenciadorEventosFatura` mantém as inscrições por tipo e entrega cada
`EventoFatura` apenas aos observadores daquele evento. Inscrições retornam uma
`AssinaturaEventoFatura`, que pode ser fechada quando o observador deixa de ser
necessário.

Os tipos publicados são `FATURA_CRIADA`, `FATURA_QUITADA`,
`LEMBRETE_VENCIMENTO`, `VENCIMENTO_HOJE` e `FATURA_ATRASADA`.

### Benefícios

- A tela não precisa consultar repetidamente o controlador nem depender de
  chamadas manuais de atualização após cada operação.
- O cadastro e a quitação notificam os interessados depois da alteração dos
  dados; a tela financeira então atualiza a tabela e os totais do caixa.
- A interface de alertas pode reagir aos lembretes sem acoplar as regras de
  notificação à janela principal.
- Observadores podem ser inscritos e removidos explicitamente, evitando manter
  telas antigas referenciadas depois da navegação.
- Novos interessados — por exemplo, registro de atividade ou outras telas —
  podem acompanhar eventos sem alterar a lógica de quitação.

### Classes afetadas pelo Observer

- `EventoFatura` e `TipoEventoFatura`: conteúdo e categorias das notificações.
- `ObservadorFatura`: contrato funcional do assinante.
- `GerenciadorEventosFatura`: inscrições, remoções e publicação dos eventos.
- `AssinaturaEventoFatura`: cancelamento explícito de uma inscrição.
- `FinanceiroController`: publisher de cadastros, quitações e lembretes diários.
- `PoliticaAlertasFatura`: decide qual alerta corresponde à data, à fatura e à
  antecedência configurada.
- `ElmixApp`: liga os publishers aos observadores, verifica lembretes na
  inicialização e acompanha a mudança de dia enquanto o programa estiver aberto.
- `TelaFinanceiroView`: observa criação e quitação, atualiza dados e abre a aba
  correta quando recebe uma fatura escolhida no alerta.
- `PopupAlertasFatura`: apresenta lembretes no topo central da janela, oferece
  `X` para dispensar cada aviso e `Ver fatura` para navegar até a fatura.
- `Configuracao`: guarda os dias de antecedência, com padrão de dois dias e
  suporte a zero (desativa apenas o lembrete pré-vencimento).
- `TelaConfigView`: permite editar os dias de antecedência.
- `ElmixApp`: valida e salva a configuração.
- `GerenciadorEventosFaturaTest` e `PoliticaAlertasFaturaTest`: verificam
  publicação, cancelamento de inscrição e cálculo dos alertas.

## Regras dos lembretes

- O padrão é avisar dois dias antes do vencimento. O número pode ser configurado
  entre 0 e 365 dias em **Configurações**.
- No próprio vencimento, o aviso diz **“A fatura vence hoje”**; a fatura só é
  considerada atrasada no dia seguinte, mantendo a regra do State.
- Depois do vencimento, há um alerta com a quantidade de dias de atraso em cada
  dia em que o programa estiver aberto.
- Os lembretes são verificados ao iniciar o sistema e diariamente enquanto ele
  estiver aberto. Ao reabrir o sistema, os alertas aplicáveis voltam a aparecer,
  mesmo se já foram fechados anteriormente.
- Quitações não geram lembretes futuros.
- Os alertas são observadores dos eventos de lembrete: dispensar o cartão fecha
  apenas a apresentação atual; **Ver fatura** abre a tela de gestão na aba
  Faturas e seleciona a fatura correspondente.

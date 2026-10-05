# Atualizações e Correções — PDV Elmix

## Contexto

O projeto do **PDV Elmix** já está desenvolvido e atualizado com diversas alterações anteriores.

**IMPORTANTE:** o projeto atual deve ser considerado a fonte principal. Antes de modificar qualquer coisa, analise a estrutura existente, os componentes, classes, banco de dados, telas, rotas e funcionalidades já implementadas.

O objetivo desta tarefa é **implementar as alterações e correções abaixo sem remover ou quebrar funcionalidades que já existem**.

Não recrie o projeto do zero.

Não substitua funcionalidades existentes sem necessidade.

Mantenha o padrão de código e a arquitetura atual do projeto.

---

# 1. Correções gerais

Verifique o projeto inteiro em busca de erros relacionados às funcionalidades descritas abaixo.

Ao implementar as alterações:

* Corrigir erros encontrados.
* Evitar duplicação desnecessária de código.
* Manter o código simples.
* Manter a arquitetura MVC existente.
* Manter o padrão visual atual.
* Não alterar funcionalidades que já estão funcionando corretamente.
* Garantir que as alterações funcionem também após reiniciar o sistema.

---

# 2. Histórico de vendas

A tela de **Histórico de Vendas** precisa permitir o gerenciamento das vendas já registradas.

## Editar venda

Deve ser possível selecionar uma venda existente e **editar seus dados**.

A edição deve atualizar corretamente os dados da venda no sistema.

Verificar também se a alteração precisa refletir no estoque e fazer a atualização de forma consistente.

## Excluir venda

Deve ser possível **excluir uma venda existente pelo Histórico de Vendas**.

Antes de excluir, apresentar uma confirmação para evitar exclusões acidentais.

Exemplo:

> Deseja realmente excluir esta venda?

Se confirmada, a venda deve ser removida corretamente do sistema.

Verificar também o impacto no estoque para evitar inconsistências.

---

# 3. Cadastro de empresa — CONCLUÍDO

No cadastro de empresa, deve ser possível armazenar também:

* Nome da empresa
* Telefone da empresa

O telefone deve ser salvo no banco de dados e aparecer quando os dados da empresa forem consultados ou editados.

---

# 4. Gerenciamento das empresas — CONCLUÍDO

Deve ser possível **editar e excluir empresas cadastradas**.

Isso deve funcionar tanto pela nova aba **Empresas** quanto pelos locais do sistema onde as empresas já são utilizadas.

Ao excluir uma empresa, apresentar confirmação antes da exclusão.

Não excluir dados históricos de vendas ou outros registros relacionados sem necessidade.

---

# 5. Nova aba "Empresas" — CONCLUÍDO

Criar uma nova aba chamada:

**Empresas**

Essa aba deverá permitir:

* Visualizar as empresas cadastradas.
* Cadastrar uma nova empresa.
* Editar uma empresa existente.
* Excluir uma empresa existente.

## Integração com Fornecedor/Empresa

A aba **Empresas** será o local principal para o cadastro completo das empresas.

As empresas cadastradas nessa aba devem aparecer automaticamente na seção atual de **Fornecedor/Empresa**.

A seção **Fornecedor/Empresa** continuará sendo um cadastro/seleção simplificado.

Ou seja:

```text
Aba Empresas
      ↓
Cadastro completo da empresa
      ↓
Empresa fica disponível em
Fornecedor/Empresa
```

Não devem existir dois cadastros independentes para a mesma empresa.

Deve existir uma única fonte de dados para as empresas.

---

# 6. Gestão — período personalizado — CONCLUÍDO

Na área de **Gestão**, atualmente existe a seleção de período.

Essa funcionalidade precisa permitir um período totalmente personalizado.

O usuário deve conseguir informar:

**Data inicial → Data final**

Exemplo:

```text
Data inicial: 18/08/2026
Data final:   17/09/2026
```

Isso é necessário porque algumas empresas trabalham com fechamento de mês diferente do mês convencional.

Portanto, não limitar a seleção somente a:

* Hoje
* Esta semana
* Este mês

Também deve existir a possibilidade de selecionar manualmente qualquer data inicial e final.

Os dados apresentados pela Gestão devem considerar exatamente o período selecionado.

---

# 7. Backup de vendas e despesas — CONCLUÍDO

O backup relacionado às vendas também deve incluir as **despesas** registradas no sistema.

Ao gerar um backup, garantir que sejam armazenados:

* Vendas
* Despesas
* Produtos
* Empresas
* Demais dados necessários para restaurar o sistema corretamente

O objetivo é que um backup contenha os dados necessários para recuperar o estado do sistema.

---

# 8. Nova organização da área de Backup — CONCLUÍDO

Atualmente existem opções separadas para:

* Salvar Backup
* Carregar Backup

Essas opções devem ficar dentro de uma única aba chamada:

**Backup**

Dentro dessa aba, os dois botões devem ficar lado a lado:

```text
┌────────────────────┐    ┌────────────────────┐
│   Gerar Backup     │    │   Carregar Backup  │
└────────────────────┘    └────────────────────┘
```

## Gerar Backup

O botão deve executar a funcionalidade atual de geração de backup.

## Carregar Backup

A funcionalidade de **Carregar Backup atualmente não está funcionando corretamente**.

Corrigir essa funcionalidade.

Ela deve permitir selecionar um backup existente e restaurar os dados corretamente.

Após a restauração:

* Atualizar os dados do sistema.
* Garantir que vendas sejam recuperadas.
* Garantir que despesas sejam recuperadas.
* Garantir que empresas sejam recuperadas.
* Garantir que produtos e demais dados incluídos no backup sejam recuperados.
* Evitar duplicações indevidas.
* Informar o usuário se a restauração foi concluída com sucesso ou se ocorreu algum erro.

---

# 9. Atalho `+` 

Alterar o comportamento do atalho:

**`+`**

Ele **não deve iniciar uma nova venda**.

O objetivo do `+` será retornar o foco para o campo onde são inseridos os valores da venda.

Ou seja:

```text
+
↓
Campo de inserção de valores
↓
Foco automático no campo
```

O campo deve ficar pronto para receber a entrada do usuário.

Não criar uma nova venda ao pressionar `+`. e deve apagar do botãod e nova venda (f1 / +) e deve ficar somento o botão como "venda em paralelo (/) "

---

# 10. Atalho `/` — Venda em paralelo

Criar uma nova funcionalidade utilizando o atalho:

**`/`**

Esse atalho deverá iniciar uma **nova venda em paralelo**.

A ideia é permitir que dois atendimentos sejam realizados simultaneamente.

## Funcionamento

Quando o usuário estiver em uma venda e pressionar `/`:

```text
PDV atual
        ↓
pressionar /
        ↓
Tela dividida
        ↓
┌──────────────────┬──────────────────┐
│      PDV 1       │      PDV 2       │
│                  │                  │
│    Venda atual   │    Nova venda    │
│                  │                  │
└──────────────────┴──────────────────┘
```

O primeiro PDV deve manter a venda que já estava sendo realizada.

O segundo PDV deve iniciar uma nova venda independente.

---

## Funcionamento das vendas paralelas

Cada lado deve possuir seu próprio:

* Carrinho
* Produtos
* Quantidades
* Total
* Valor pago
* Troco
* Estado da venda

Uma venda não deve interferir na outra.

Exemplo:

```text
PDV 1
Produto A
Produto B
Total: R$ 50

PDV 2
Produto C
Total: R$ 20
```

As duas vendas devem permanecer independentes.

---

## Finalização de uma venda paralela

Se o usuário finalizar a venda de um dos lados:

```text
PDV 1 → Finalizado
PDV 2 → Continua aberto
```

O sistema deve:

1. Salvar a venda finalizada.
2. Atualizar os dados necessários.
3. Fechar somente aquele PDV.
4. Retornar o outro PDV para ocupar a tela inteira.

Exemplo:

```text
Antes:

┌──────────────┬──────────────┐
│    PDV 1     │    PDV 2     │
│              │              │
│  FINALIZAR   │   CONTINUA   │
└──────────────┴──────────────┘

Depois:

┌──────────────────────────────┐
│                              │
│            PDV 2             │
│                              │
│          CONTINUA             │
│                              │
└──────────────────────────────┘
```

A venda que permaneceu aberta **não pode ser perdida**.

---

# 11. Regras importantes para o PDV paralelo — CONCLUÍDO

Ao implementar o `/`:

* Não duplicar vendas.
* Não misturar carrinhos.
* Não misturar produtos.
* Não misturar valores.
* Não perder a venda original.
* Cada PDV deve possuir seu próprio estado.
* Finalizar um PDV não deve finalizar o outro.
* Depois que um PDV for finalizado, o outro deve voltar para tela inteira.
* Se ambos forem finalizados, retornar ao comportamento normal do PDV.
* O sistema deve continuar permitindo iniciar novas vendas normalmente.

---

# 12. Compatibilidade com funcionalidades existentes

Todas as funcionalidades já existentes devem continuar funcionando.

Especialmente:

* Nova venda
* Finalização de venda
* Histórico
* Gestão
* Despesas
* Produtos
* Estoque
* Empresas
* Backup
* Configurações
* Atalhos existentes

Não remover funcionalidades existentes para implementar as novas.

---

# 13. Banco de dados

Antes de alterar o banco de dados, verificar a estrutura atual.

Caso seja necessário adicionar campos ou tabelas:

* Fazer a alteração de forma compatível com os dados existentes.
* Não apagar dados atuais.
* Criar as estruturas necessárias para empresas e telefone.
* Garantir que vendas e despesas sejam incluídas no backup.
* Garantir que o sistema continue funcionando com dados já cadastrados.

continuar usando o .ser para guardar dados (ou qual ja esta usando)

---

# 14. Interface

Manter o estilo visual atual do sistema.

As novas funcionalidades devem seguir o mesmo padrão existente de:

* Cores
* Botões
* Bordas
* Espaçamentos
* Tipografia
* Ícones
* Mensagens
* Modais

Não criar uma interface visual completamente diferente.

A nova aba **Empresas** e a nova aba **Backup** devem parecer partes naturais do sistema atual.

---

# 15. Checklist final

Antes de considerar a tarefa concluída, verificar:

* [ ] É possível editar uma venda pelo Histórico.
* [ ] É possível excluir uma venda pelo Histórico.
* [ ] Exclusão de venda possui confirmação.
* [ ] Empresa possui campo de telefone.
* [ ] É possível editar uma empresa.
* [ ] É possível excluir uma empresa.
* [ ] Existe a aba Empresas.
* [ ] É possível cadastrar empresas pela aba Empresas.
* [ ] Empresas cadastradas aparecem em Fornecedor/Empresa.
* [ ] Não existem cadastros independentes duplicados de empresas.
* [ ] Gestão permite escolher data inicial e data final manualmente.
* [ ] Períodos como dia 18 até dia 17 funcionam corretamente.
* [ ] Backup inclui despesas.
* [ ] Aba Backup contém Gerar Backup e Carregar Backup.
* [ ] Gerar Backup funciona.
* [ ] Carregar Backup funciona corretamente.
* [ ] Restauração recupera os dados necessários.
* [ ] O atalho `+` foca o campo de inserção de valores.
* [ ] O `+` não inicia nova venda.
* [ ] O atalho `/` inicia uma venda em paralelo.
* [ ] Dois PDVs podem funcionar simultaneamente.
* [ ] Os carrinhos dos dois PDVs são independentes.
* [ ] Finalizar um PDV não finaliza o outro.
* [ ] O PDV restante volta para tela inteira.
* [ ] Nenhum dado é perdido durante a operação.
* [ ] Funcionalidades anteriores continuam funcionando.

---

# Regra principal

**Analise primeiro o projeto atual e faça as alterações sobre a implementação existente.**

Não reescreva o projeto inteiro.

Não remova funcionalidades existentes.

Não altere o visual sem necessidade.

Implemente somente as mudanças necessárias para atender aos requisitos deste documento.

Ao finalizar, faça uma verificação geral para garantir que as alterações não causaram regressões em outras funcionalidades.

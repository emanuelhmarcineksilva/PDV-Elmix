# Ajustes — Venda Paralela e Empresas

Faça as seguintes alterações no projeto atual, mantendo a identidade visual e a estrutura já existentes.

## 1. Modal de Venda Paralela

O modal da **Venda em Paralelo** está muito estreito e comprido verticalmente.

Ajuste o tamanho para que ele tenha aproximadamente **40% da largura da tela**, mantendo uma altura proporcional e mais compacta.

### Regras:
- Aumentar o `width` do modal.
- Evitar que ele fique excessivamente comprido verticalmente.
- Manter o modal centralizado.
- Aproveitar melhor o espaço horizontal.
- Manter os campos e botões organizados dentro do novo tamanho.
- Não alterar a funcionalidade da venda paralela.
- Manter exatamente a identidade visual atual do sistema:
  - verde suave;
  - verde escuro;
  - campos claros;
  - bordas arredondadas;
  - botões no mesmo padrão do PDV.

---

## 2. Tela de Empresas

Alterar a forma como as empresas cadastradas são exibidas.

Quero que a tela de **Empresas** tenha uma estrutura semelhante a uma tela de **CRUD**, seguindo o visual atual do sistema.

### Funcionamento

A tela deve mostrar uma lista/tabela de empresas cadastradas.

Cada empresa deve apresentar suas principais informações, por exemplo:

- Nome da empresa
- Telefone
- Demais informações já existentes no cadastro

Ao selecionar uma empresa, deve ser possível:

- **Visualizar**
- **Editar**
- **Excluir**

A edição deve abrir uma área/formulário com os dados atuais da empresa preenchidos, permitindo alterá-los e salvar.

A exclusão deve solicitar confirmação antes de remover a empresa.

### Identidade visual

A nova tela deve seguir **a mesma identidade visual do restante do sistema**, utilizando:

- Verde suave como cor principal.
- Verde escuro para títulos e ações.
- Campos claros.
- Botões arredondados.
- Bordas arredondadas.
- Mesma tipografia e espaçamento utilizados atualmente.
- Layout simples e limpo.

Não criar uma interface com aparência diferente do restante do sistema.

### Importante

Antes de implementar, analise como as empresas já estão cadastradas e armazenadas no projeto.

**Não crie um segundo sistema de empresas.** A nova tela deve utilizar os mesmos dados já existentes.

As alterações feitas na aba **Empresas** devem refletir automaticamente nos locais onde a empresa é utilizada, como **Fornecedor/Empresa**.

Não remova funcionalidades existentes e não altere outras partes do sistema sem necessidade.
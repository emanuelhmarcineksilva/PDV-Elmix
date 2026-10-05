PDV de vendas Elmix


Elmix, uma loja de produtos naturais.

Cores do sistema:
verde suave, campo com branco (levemente bege), texto sempre preto,


Ideia: dese ser uma calculadora com campo de produtos onde se não escrevermos o produto ao certo ela escreve diversos por padrão e deve ter um campo de troco para quando eu finaliza a venda. 

f1 para iniciar uma nova venda no aplicativo.
f3 muda para campo de valor (para inserir quanto a pessoa pagou).
f4 para filalizar uma venda no aplicativo (ja retorma o troco e quando aperta mais uma vez enter inicia na tela vazia para uma nova venda).


quando eu for fechar o caixa deve aparecer uma opção para gerar um documento TXT com informações da venda do dia, quardar a data do dia e quanto foi vendido, mas deve aparecer bem simples de entender nesse documento pois ess não é para backup.

coloque uma opção de backup  tbm (na pagina inicial) que podemos escolher a data inicial e final no sistema.

Pagina inicial - cor principal verde bem suave e claro
campos: 
- Header : com nome "ELMIX" no header (cor vermelho suave) - titulo
- Meio: Uma passagem biblica "poderia ser uma cada dia por API" - Texto grande e centralizado no meio da tela, cor verde um pouco mais escuro;
- Footer: texto pequeno e verde escuro "Orquestrado por Emanuel Henrique" na linha de baixo meu GitHub "https://github.com/emanuelhmarcineksilva"
- Direita: Uma barra com Botões (na parte do meio para baixo) de iniciar venda, historico de vendas,  cor verde sou um pouco mais escura que o padrão do site
- Esquerda: uma barra igual da direita, e mesma cor, com botoes (desde cima até meio), deve ter conta, salvar backup, entrar backup, configurações;
- botoes: cor brancos e texto verde escuro; borda aredondadas

Botões:
- conta: deve mostrar informações da loja "nome", endereço ([Endereço](https://www.google.com/search?sca_esv=f2191a8c18cbf8a0&sxsrf=APpeQntB-EmPkHQ7tKzA8J9LQWIWZ9hwAw:1782485295908&q=elmix+com%C3%A9rcio+de+produtos+naturais,+pinhais,+jardim+am%C3%A9lia+endere%C3%A7o&ludocid=1622384785732313770&sa=X&ved=2ahUKEwjr1YSXk6WVAxWDNrkGHTkMLUYQ6BN6BAguEAI): R. Sebastião Stancki da Luz Júnior, 70 - Jardim Amélia, Pinhais - PR, 83330-360), e um link para o futuro site que tera;
- configurações: para mudar a cor padrão do site, temas escuros etcs, mudar nome da Loja, mudar a borda aredondada dos botões, ativar ou desativar efeitos da tela (animalções), e mudar link do site, e outras coisas pertinentes para a configuração;
- iniciar venda vai para tela do PDV (onde vai ter um botão de sair dessa tela para voltar para principal) mas pode clicar f1 para entrar a iniciar uma nova venda
- dentro da tela de PDV de ventas: Nova venda, para iniciar uma nova venda cor verde escuro e texto branco
- PDV: finalizar venda para salvar a venda (ou f4), cor verde escuro e texto branco,



Animação:
no sistema quero que tenha a animação de umas folhas caindo (folhas bem pequenas = pontos verdes passando cada um com uma tonalidade de verde aleatoria entre claro e escuro, cada pontinho queve spaunar de forma aleatoria de uma lado ao outro do sistema e dele ser lentos temdo cada um tbm uma velocidade variada entre lento meio lento muito lento super lento) para a tela inicial onde tem a passagem biblica, mas na tela inteira, crie isso.

os botões quando coloca o mause tbm devem ou ficar um pouco opacos ou mais claro ou escuros, de acordo com o que dar melhor aparecenia e harmonia para o sistema inteiro.

campos selecionado e digitando dever criar uma borda tbm bem bonitinha

Estrutura:
codigo em vaja no Estilo MVC.
codigo extremamente simples e comentado.
Não Usar FXML, mas o javaFx.
vamos persistir os dados em .ser, ou data (tendo um arquivo desses como o proprio banco de dados do sistema em si).

não implemente a animação das folhas ainda, vamos entregar o MVP funcional primeiro
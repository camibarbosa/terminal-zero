package camila.barbosa.backend.game;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Component;

@Component 
public class GameCatalog {
    public record Step(String goal, String command, List<String> alternatives, 
        String explanation, List<String> output, String success) {
    }

    public record Station(String id, String station, String title, String subtitle, String sticker,
        String intro, String concept, List<Step> steps, Map<String,List<String>> diagnostics) {
    }

    private Step step(String goal, String command, String explanation, String success, String... output) {
        return new Step(goal, command, List.of(), explanation, List.of(output), success);
    }

    private final List<Station> stations = List.of(
        new Station("linux","Estação 01","Permissões no Servidor","Linux · listar arquivos e liberar execução para o proprietário","Servidor Linux",
            "O deploy das 17h59 gerou um alerta na Sala de Controle. Nesta estação Linux, precisamos localizar e executar o script de diagnóstico.",
            "No Linux, ls -la exibe as permissões, chmod u+x concede execução apenas ao proprietário e ./ executa o script no diretório atual.",
            List.of(
                step("Listar os arquivos e verificar suas permissões","ls -la","Use ls -la para lista os arquivos mostrando as permissões de leitura ‘r’, " +
                        "escrita ‘w’ e execução ‘x’ para proprietário, grupo e outros.","Veja que diagnostico.sh está com -rw-r--r--: o proprietário tem leitura e escrita, " +
                        "mas não tem execução (x).","-rw-r--r-- 1 operadora devops leia_me.txt","-rw-r--r-- 1 operadora devops diagnostico.sh"),
                step("Liberar execução restrita ao proprietário do arquivo","chmod u+x diagnostico.sh","Use chmod u+x diagnostico.sh (o u+x adiciona permissão de execução apenas para o usuário proprietário do arquivo, sem liberar para grupo ou outros).","Permissão concedida apenas ao proprietário com segurança.","Permissão atualizada: -rwxr--r-- diagnostico.sh (execução restrita ao proprietário)"),
                step("Executar o script de diagnóstico","./diagnostico.sh","Agora execute ./diagnostico.sh (o prefixo ./ indica que o script deve ser rodado a partir do diretório atual).","Pista confirmada para a Estação 02.","[DIAGNÓSTICO] Falha detectada: o deploy das 17h59 alterou a rota no repositório Git.")
            ), Map.of()),
        new Station("git","Estação 02","Controle de Versão","Git · auditar histórico e reverter commit com segurança","Repositório Git",
            "O diagnóstico do Linux apontou que o deploy das 17h59 alterou o código no repositório Git e quebrou a rota de pagamentos.",
            "git log --oneline lista o histórico; git show inspeciona as alterações; git revert HEAD cria um commit inverso sem apagar o histórico.",
            List.of(
                step("Consultar os últimos commits registrados","git log --oneline","Use git log --oneline para exibir o histórico resumindo cada commit em uma linha com seu código identificador.","Identificamos o commit c9912a0 no topo da branch (HEAD).","c9912a0 (HEAD) deploy 17h59 - alteração na rota de pagamento","8f21d04 versão estável"),
                step("Inspecionar as linhas alteradas no commit","git show","Use git show para exibir os detalhes e as linhas removidas ‘-’ e adicionadas ‘+’ dentro do último commit salvo.","Confirmamos a alteração que causou a falha.","commit c9912a0","- rota /pagamentos (porta 5432)","+ rota /erro_500 (porta 9999)"),
                step("Reverter o commit preservando o histórico","git revert HEAD","Use git revert HEAD para criar um novo commit aplicando o inverso do último commit HEAD, sem reescrever o histórico.","Código revertido com rastreabilidade na Estação 02.","[main 4d10e22] Revert deploy 17h59","1 arquivo restaurado")
            ), Map.of()),
        new Station("docker","Estação 03","Containers e Prontidão do Banco","Docker · verificar containers, logs e aceitar conexões com pg_isready","Containers Docker",
            "Mesmo após reverter o código no Git, o sistema não salva dados. Na Estação 03, a API Java e o banco PostgreSQL rodam isolados em containers Docker.",
            "docker ps -a lista containers parados; docker logs revela o motivo; docker start inicia; pg_isready confirma se o PostgreSQL aceita conexões.",
            List.of(
                step("Listar todos os containers (ativos e parados)","docker ps -a","Use docker ps -a para lista todos os containers do servidor; a opção -a inclui os que estão parados no estado Exited.","A API está em execução, mas o container postgres está parado (Exited).","NAMES       STATUS","spring-api  Up 2 hours","postgres    Exited (0) 3 minutes ago"),
                step("Consultar os logs do container do banco","docker logs postgres","Antes de iniciar, use docker logs postgres para ler o histórico de mensagens internas do container.","O banco parou sem corromper os dados.","[postgres] desligamento limpo concluído, volumes de dados íntegros"),
                step("Iniciar o container do banco de dados","docker start postgres","Use docker start postgres para inicia o container que estava parado.","O container subiu, mas precisamos confirmar se o banco dentro dele já aceita conexões.","postgres"),
                step("Testar se o PostgreSQL está aceitando conexões","docker exec postgres pg_isready","O docker ps mostra apenas se o container está em execução, mas não garante que o PostgreSQL já esteja aceitando conexões. Use docker exec postgres pg_isready para testar a prontidão do banco.","Prontidão confirmada: o PostgreSQL está aceitando conexões.","/var/run/postgresql:5432 - accepting connections")
            ), Map.of()),
        new Station("cicd","Estação 04","Pipeline de Automação (CI/CD)","Maven & Pipeline · rodar testes, empacotar e publicar","Pipeline CI/CD",
            "Na Estação 04 fica a esteira de integração e entrega contínua (CI/CD): cada alteração passa por testes automatizados antes de gerar o pacote e ir para produção.",
            "mvn test executa os testes automatizados; mvn package gera o artefato .jar. A configuração é corrigida e o deploy é simulado pelo terminal.",
            List.of(
                step("Executar a bateria de testes automatizados","mvn test","Use mvn test para compilar o projeto Java e executa os testes automatizados.","A esteira travou o processo porque a porta na configuração estava 5433.","[INFO] Rodando PagamentoRepositoryTest","[ERROR] Falha: porta configurada no teste (5433) difere da esperada (5432)","BUILD FAILURE"),
                step("Corrigir a configuração da porta para 5432","corrigir db.port 5432","Digite corrigir db.port 5432 para ajustar a propriedade db.port no arquivo de configuração da aplicação para a porta esperada pelo banco.","Propriedade ajustada. Vamos reexecutar os testes.","db.port=5433  ->  db.port=5432"),
                step("Revalidar a bateria de testes","mvn test","Execute mvn test novamente para comprovar que os testes passam.","Todos os 12 testes passaram.","[INFO] Tests run: 12, Failures: 0","BUILD SUCCESS"),
                step("Gerar o pacote da aplicação (.jar)","mvn package","Use mvn package para empacotar o código compilado e aprovado em um arquivo executável .jar.","Artefato app.jar gerado.","[INFO] Empacotando aplicação...","[INFO] Criado: target/app.jar","BUILD SUCCESS"),
                step("Publicar o pacote aprovado","deploy app.jar","Digite deploy app.jar para simular a publicação do pacote aprovado no ambiente de produção.","Estação 04 concluída.","Implantando app.jar no servidor...","Deploy concluído com sucesso.")
            ), Map.of()),
        new Station("saude","Estação 05","Observabilidade e Causa Raiz","Health check, filtro de logs com grep e mitigação operacional","Observabilidade",
            "Na Estação 05 monitoramos a saúde do ambiente. O servidor continua lento: precisamos consultar o health check, investigar a causa nos logs e destravar o serviço.",
            "curl consulta o health check; grep localiza erros no log; docker restart é uma mitigação temporária, não a correção definitiva.",
            List.of(
                step("Consultar o endpoint de saúde (Health Check)","curl localhost:8080/actuator/health","Use curl localhost:8080/actuator/health para requisitar a rota de diagnóstico do Spring Boot Actuator para verificar o estado dos componentes.","O banco está UP, mas o serviço srv-relatorios está DOWN.","{ \"status\": \"DEGRADED\",","  \"db\": \"UP\",","  \"srv-relatorios\": \"DOWN\" }"),
                step("Filtrar as linhas de erro no arquivo de log","grep \"ERROR\" app.log","Use grep \"ERROR\" app.log para filtrar o arquivo app.log exibindo apenas as linhas que contêm a palavra ERROR.","Identificamos a causa raiz: falta de timeout no loop de retentativa do serviço (e descobrimos o cochilo do Pip às 17h59).","[17:59] ERROR srv-relatorios em loop infinito de retentativa (falta configuração de timeout)","[17:59] ERROR auditoria: sensor detectou mascote Pip dormindo sobre o teclado do servidor"),
                step("Aplicar mitigação reiniciando o serviço travado","docker restart srv-relatorios","Use docker restart srv-relatorios. O restart resolve temporariamente o processo travado na memória, mas não substitui investigar o log e corrigir a falta de timeout no código.","Serviço destravado e causa raiz documentada.","srv-relatorios","Mitigação aplicada: processo reiniciado e CPU normalizada (12%).")
            ), Map.of())
    );

    public List<Station> stations() { 
        return stations; 
    }

    public Optional<Station> find(String id) { 
        return stations
        .stream()
        .filter(s -> s.id().equals(id))
        .findFirst(); 
    }
}

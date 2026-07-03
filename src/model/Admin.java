package model;

import bd.BancoDados;
import java.util.List;

/**
 * Representa o perfil de Administrador (Admin) no sistema, estendendo a infraestrutura de sessão.
 * <p>
 * Esta classe encapsula as regras de negócio e chamadas de back-end para controle,
 * moderação e auditoria global da plataforma de delivery. Um administrador ativo pode
 * gerenciar o status de banimento de contas, promover ou rebaixar usuários, além de extrair
 * métricas consolidadas diretamente do banco de dados.
 * </p>
 * * @author Arthur, Felipe, Davi
 * @version 1.2
 */
public class Admin extends Login {

    /**
     * Construtor parametrizado para inicialização e registro do perfil do Administrador.
     * Repassa as credenciais de identificação e autenticação diretamente para o escopo de sessão da superclasse.
     *
     * @param email O endereço eletrônico (e-mail) institucional ou administrativo do admin.
     * @param senha A chave de acesso/senha associada para validação de segurança.
     * @param user  O nome de exibição ou alcunha (username) do moderador.
     */
    public Admin(String email, String senha, String user) {
        super(email, senha, user);
    }

    /**
     * Aplica ou remove uma restrição punitiva permanente (banimento) em uma conta de usuário do sistema.
     *
     * @param idUsuario  O identificador único do usuário afetado na tabela do banco de dados.
     * @param deveBanir  true para banir e bloquear o usuário, false para desbanir.
     * @return true se a operação de atualização foi bem-sucedida, false caso contrário.
     */
    public boolean definirBanimentoConta(int idUsuario, boolean deveBanir) {
        return BancoDados.definirBanimento(idUsuario, deveBanir);
    }

    /**
     * Remove os privilégios administrativos de uma conta padrão, retornando-a ao nível de cliente comum.
     * <p>Ação restrita e válida apenas para execução sob o escopo de um Admin Master.</p>
     *
     * @param idUsuario O identificador único do administrador a ser rebaixado.
     * @return true se a alteração de cargo foi efetuada com sucesso, false caso contrário.
     */
    public boolean rebaixarAdministrador(int idUsuario) {
        return BancoDados.rebaixarAdmin(idUsuario, "cliente");
    }

    /**
     * Promove um usuário comum cadastrado na plataforma ao cargo e privilégios de administrador padrão.
     *
     * @param idUsuario O identificador único do usuário a ser promovido.
     * @return true se a promoção hierárquica foi registrada com sucesso, false caso contrário.
     */
    public boolean promoverUsuarioParaAdmin(int idUsuario) {
        return BancoDados.promoverParaAdmin(idUsuario);
    }

    /**
     * Cria e registra as credenciais de acesso de um novo administrador padrão diretamente na base de dados.
     *
     * @param nome  Nome completo do novo colaborador administrativo.
     * @param email Endereço de e-mail exclusivo que servirá de login de acesso.
     * @param senha Senha em texto puro a ser associada à conta.
     * @return true se o cadastro foi efetuado, false se houver falhas ou o e-mail já estiver em uso.
     */
    public boolean criarNovoAdministrador(String nome, String email, String senha) {
        return BancoDados.criarNovoAdmin(nome, email, senha);
    }

    /**
     * Lista todos os registros de usuários filtrados por uma categoria específica para fins de moderação em tempo real.
     *
     * @param tipo O tipo de usuário desejado (ex: 'cliente', 'restaurante', 'entregador', 'admin').
     * @return Uma {@link List} contendo os registros encapsulados na estrutura {@link bd.BancoDados.UsuarioAdmin}.
     */
    public List<BancoDados.UsuarioAdmin> listarUsuariosParaModeracao(String tipo) {
        return BancoDados.listarUsuariosPorTipo(tipo);
    }

    /**
     * Recupera a quantidade total de contas cadastradas sob a categoria de clientes comuns.
     * @return O número de clientes registrados (int).
     */
    public int obterContagemClientes() {
        return BancoDados.contarUsuariosPorTipo("cliente");
    }

    /**
     * Recupera a quantidade total de contas cadastradas sob a categoria de entregadores parceiros.
     * @return O número de entregadores registrados (int).
     */
    public int obterContagemEntregadores() {
        return BancoDados.contarUsuariosPorTipo("entregador");
    }

    /**
     * Recupera a quantidade total de estabelecimentos cadastrados na plataforma.
     * @return O número total de restaurantes mapeados (int).
     */
    public int obterContagemRestaurantes() {
        return BancoDados.contarTotalRestaurantesCadastrados();
    }

    /**
     * Recupera a quantidade total de contas administrativas existentes (incluindo admins padrão e master).
     * @return O número total de administradores (int).
     */
    public int obterContagemAdmins() {
        return BancoDados.contarTotalAdmins();
    }

    /**
     * Recupera o volume histórico total de pedidos computados no ecossistema da plataforma.
     * @return A contagem acumulada de pedidos realizados (int).
     */
    public int obterTotalPedidos() {
        return BancoDados.contarTotalPedidos();
    }

    /**
     * Calcula dinamicamente a soma do faturamento monetário bruto transacionado por todos os pedidos da plataforma.
     * @return O valor monetário total calculado em formato double.
     */
    public double obterFaturamentoTotal() {
        return BancoDados.calcularFaturamentoTotal();
    }
}
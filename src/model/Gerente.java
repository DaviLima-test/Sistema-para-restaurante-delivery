package model;

/**
 * Representa o perfil de Gerente no sistema, estendendo a infraestrutura de sessão.
 * <p>
 * Esta classe encapsula as responsabilidades administrativas e operacionais de um
 * gestor de restaurante. Permite o controle total sobre o ciclo de vida dos produtos
 * do cardápio (inclusão, alteração, remoção), além de solicitar o vínculo cadastral
 * de novos estabelecimentos e extrair balanços financeiros.
 * </p>
 * * @author Arthur, Felipe, Davi
 * @version 1.1
 */
public class Gerente extends Login {

    /**
     * Construtor parametrizado para inicialização e registro do perfil do Gerente.
     * Repassa as credenciais de identificação diretamente para o escopo de sessão da superclasse.
     *
     * @param email O endereço eletrônico (e-mail) corporativo do gerente.
     * @param senha A chave de acesso/senha associada à conta.
     * @param user  O nome de exibição ou alcunha do administrador.
     */
    public Gerente(String email, String senha, String user) {
        super(email, senha, user);
    }

    /**
     * Efetua a inclusão de um novo prato ou insumo comercializável ao portfólio
     * do restaurante gerenciado.
     *
     * @param produto O objeto {@link Produto} configurado a ser injetado no cardápio.
     */
    public void AdicionarProduto(Produto produto) {
        // Lógica simplificada
    }

    /**
     * Modifica as características descritivas de um prato já existente no cardápio.
     *
     * @param nome      O novo título ou identificação comercial do prato.
     * @param descricao O novo detalhamento técnico de ingredientes ou composição.
     */
    public void AtualizarProduto(String nome, String descricao) {
        // Lógica simplificada
    }

    /**
     * Remove de forma definitiva um item específico da listagem de ofertas
     * ativas do restaurante.
     *
     * @param produto O objeto {@link Produto} que será desvinculado e excluído.
     */
    public void RemoverProdutos(Produto produto) {
        // Lógica simplificada
    }

    /**
     * Abre uma requisição formal no sistema para registrar e vincular um novo
     * estabelecimento sob a tutela deste perfil de gerência.
     *
     * @param nomeRestaurante O nome fantasia do comércio alimentício a ser criado.
     */
    public void SolicitarCadastroRest(String nomeRestaurante) {
        // Lógica simplificada
    }

    /**
     * Compila e consolida os dados de vendas, faturamentos e fluxo de caixa
     * para gerar o relatório de desempenho do estabelecimento.
     */
    public void GerarRelatorio() {
        // Lógica simplificada
    }
}
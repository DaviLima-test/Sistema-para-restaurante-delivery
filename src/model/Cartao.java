package model;

import bd.BancoDados;
import view.Telabase;
import java.util.ArrayList;

/**
 * Representa um Cartão de Crédito ou Débito vinculado a uma conta no sistema.
 * <p>
 * Concentra os dados financeiros confidenciais necessários para transações monetárias,
 * englobando número, nome do titular, data de validade, código de verificação (CVV) e a bandeira.
 * Possui mecanismos internos de adequação e higienização de strings para evitar falhas de formatação,
 * além de gerenciar a marcação de cartão principal para faturamento automatizado.
 * </p>
 * * @author Arthur, Felipe, Davi
 * @version 1.1
 */
public class Cartao {

    /** O número completo impresso na frente do cartão de crédito. */
    private String numero;

    /** O nome do proprietário ou titular do cartão, exatamente como impresso no plástico. */
    private String titular;

    /** A data de expiração/validade do cartão (normalmente formatada como MM/AAAA ou MM/AA). */
    private String validade;

    /** O código de verificação do cartão (CVV), usado como fator de segurança na validação. */
    private String cvv;

    /** A empresa operadora/bandeira do cartão (Ex: Visa, Mastercard, Elo). */
    private String bandeira;

    /** Flag indicativa se este cartão é a opção preferencial/principal de pagamento do usuário. */
    private boolean principal;

    /**
     * Construtor completo e parametrizado para inicialização e higienização das propriedades do cartão.
     * Invoca automaticamente o método {@link #adequarDadosDoCartao()} ao ser instanciado.
     *
     * @param numero    O número de identificação do cartão.
     * @param titular   O nome do dono impresso no cartão.
     * @param validade  A data de vencimento/expiração do insumo.
     * @param cvv       O código verificador de segurança de 3 ou 4 dígitos.
     * @param bandeira  A marca/bandeira que opera o cartão.
     * @param principal Define se este é o método de pagamento padrão do cliente.
     */
    public Cartao(String numero, String titular, String validade, String cvv, String bandeira, boolean principal) {
        this.numero = numero;
        this.titular = titular;
        this.validade = validade;
        this.cvv = cvv;
        this.bandeira = bandeira;
        this.principal = principal;
        adequarDadosDoCartao();
    }

    /**
     * Extrai e isola as últimas 4 posições numéricas da string do cartão.
     * Utilizado para exibição mascarada segura em componentes visuais da interface (Ex: "•••• 4321").
     * @return Uma string contendo os 4 caracteres finais, ou "0000" caso o valor em memória seja inválido.
     */
    public String getQuatroUltimosDigitos() {
        if (numero == null || numero.length() < 4) return "0000";
        return numero.substring(numero.length() - 4);
    }

    /**
     * Consulta e coleta a coleção completa de todos os cartões cadastrados do usuário logado.
     * Delega a leitura física diretamente para a camada de persistência de dados.
     * @return Um {@link ArrayList} contendo objetos do tipo {@link Cartao}.
     */
    public static ArrayList<Cartao> getCartoes(){
        return BancoDados.GetCartoes();
    }

    /**
     * Executa a higienização, sanitização e truncamento seguro de dados textuais do cartão.
     * Remove espaços em branco desnecessários e limita o tamanho máximo de caracteres
     * para coincidir com as restrições de tamanho (VARCHAR/CHAR) estipuladas na tabela do banco de dados.
     */
    public void adequarDadosDoCartao() {
        if (this.numero != null) {
            this.numero = this.numero.trim().replace(" ", "");
            this.numero = this.numero.substring(0, Math.min(this.numero.length(), 16));
        }

        if (this.validade != null) {
            this.validade = this.validade.trim();
            this.validade = this.validade.substring(0, Math.min(this.validade.length(), 7));
        }

        if (this.cvv != null) {
            this.cvv = this.cvv.trim();
            this.cvv = this.cvv.substring(0, Math.min(this.cvv.length(), 4));
        }

        if (this.bandeira != null) {
            this.bandeira = this.bandeira.trim();
            this.bandeira = this.bandeira.substring(0, Math.min(this.bandeira.length(), 20));
        }
    }

    /**
     * Recupera a string tratada correspondente ao número do cartão.
     * @return O número do cartão (String).
     */
    public String getNumero() { return numero; }

    /**
     * Recupera o nome literal do portador do cartão.
     * @return O nome do titular (String).
     */
    public String getTitular() { return titular; }

    /**
     * Recupera a string de data correspondente à validade estipulada.
     * @return A data de validade (String).
     */
    public String getValidade() { return validade; }

    /**
     * Recupera o código de segurança e verificação confidencial (CVV).
     * @return O CVV do cartão (String).
     */
    public String getCvv() { return cvv; }

    /**
     * Recupera a marca/empresa operadora do cartão.
     * @return A bandeira do cartão (String).
     */
    public String getBandeira() { return bandeira; }

    /**
     * Verifica se este cartão específico está definido como o favorito do perfil logado.
     * @return true se for o cartão padrão principal, false caso contrário.
     */
    public boolean isPrincipal() { return principal; }

    /**
     * Altera ou redefine o status de prioridade deste cartão na conta.
     * @param principal booleano definindo se ele deve assumir como cartão preferencial.
     */
    public void setPrincipal(boolean principal) { this.principal = principal; }

    /**
     * Localiza e instancia o cartão preferencial de faturamento salvo para a conta do usuário ativo.
     * @return O objeto {@link Cartao} marcado como principal no banco, ou null se não houver registros.
     */
    public static Cartao GetPrincipal(){
        return BancoDados.GetCartaoPrincipal();
    }

    /**
     * Transmite o estado e os dados encapsulados nesta instância corrente para gravação
     * física e persistência na tabela de cartões da base de dados.
     */
    public void SalvarCartao(){
        BancoDados.salvarCartao(this);
    }
}
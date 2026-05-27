package selenium.pages;

public class CadastroData {
    private final String nome;
    private final String sobrenome;
    private final String telefone;
    private final String username;
    private final String senha;
    private final String rua;
    private final String numero;
    private final String bairro;
    private final String complemento;
    private final String cidade;
    private final String uf;

    public CadastroData(String nome, String sobrenome, String telefone, String username, String senha,
                        String rua, String numero, String bairro, String complemento, String cidade, String uf) {
        this.nome = nome;
        this.sobrenome = sobrenome;
        this.telefone = telefone;
        this.username = username;
        this.senha = senha;
        this.rua = rua;
        this.numero = numero;
        this.bairro = bairro;
        this.complemento = complemento;
        this.cidade = cidade;
        this.uf = uf;
    }

    public String getNome() { return nome; }
    public String getSobrenome() { return sobrenome; }
    public String getTelefone() { return telefone; }
    public String getUsername() { return username; }
    public String getSenha() { return senha; }
    public String getRua() { return rua; }
    public String getNumero() { return numero; }
    public String getBairro() { return bairro; }
    public String getComplemento() { return complemento; }
    public String getCidade() { return cidade; }
    public String getUf() { return uf; }
}

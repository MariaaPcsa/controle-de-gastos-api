package com.maria.finance.user.infrastructure.excel;

public class UserImportError {

    private int linha;
    private String tipo;
    private String mensagem;
    private String email;

    public UserImportError(int linha, String tipo, String mensagem, String email) {
        this.linha = linha;
        this.tipo = tipo;
        this.mensagem = mensagem;
        this.email = email;
    }

    public int getLinha() { return linha; }
    public String getTipo() { return tipo; }
    public String getMensagem() { return mensagem; }
    public String getEmail() { return email; }
}

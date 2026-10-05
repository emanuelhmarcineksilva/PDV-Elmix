package pdv.demo4.model;

import java.io.Serializable;
import java.util.UUID;

/**
 * Representa fornecedor/empresa vinculada a lançamentos de SAIDA.
 * Persistida via serialização em financeiro.ser.
 */
public class Empresa implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;         // UUID
    private String nomeRazao;  // Nome fantasia / razão social
    private String cnpjCpf;    // CNPJ ou CPF (opcional no MVP)
    private String telefone;   // Telefone do contato da empresa
    private boolean ativo;

    public Empresa(String nomeRazao, String cnpjCpf) {
        this(nomeRazao, cnpjCpf, null);
    }

    public Empresa(String nomeRazao, String cnpjCpf, String telefone) {
        this.id = UUID.randomUUID().toString();
        this.nomeRazao = nomeRazao == null ? "" : nomeRazao.trim();
        this.cnpjCpf = cnpjCpf == null ? "" : cnpjCpf.trim();
        this.telefone = telefone == null ? "" : telefone.trim();
        this.ativo = true;
    }

    // Para desserialização / edição
    public Empresa() {
        this.id = UUID.randomUUID().toString();
        this.telefone = "";
        this.ativo = true;
    }

    public String getId() { return id; }
    public String getNomeRazao() { return nomeRazao; }
    public void setNomeRazao(String nomeRazao) { this.nomeRazao = nomeRazao == null ? "" : nomeRazao.trim(); }
    public String getCnpjCpf() { return cnpjCpf; }
    public void setCnpjCpf(String cnpjCpf) { this.cnpjCpf = cnpjCpf == null ? "" : cnpjCpf.trim(); }
    public String getTelefone() { return telefone == null ? "" : telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone == null ? "" : telefone.trim(); }
    public boolean isAtivo() { return ativo; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (nomeRazao != null && !nomeRazao.isBlank()) {
            sb.append(nomeRazao);
        }
        if (telefone != null && !telefone.isBlank()) {
            if (!sb.isEmpty()) sb.append(" - ");
            sb.append(telefone);
        }
        if (cnpjCpf != null && !cnpjCpf.isBlank()) {
            if (!sb.isEmpty()) sb.append(" (");
            sb.append(cnpjCpf);
            if (!sb.isEmpty()) sb.append(")");
        }
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Empresa)) return false;
        Empresa e = (Empresa) o;
        return id != null && id.equals(e.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}

package bancario.controller;

import bancario.model.Cliente;
import bancario.model.ContaBancaria;
import bancario.util.ArquivoBinario;
import bancario.util.ValidadorCPF;

import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class BancoController {
    private Map<String, Cliente> clientes = ArquivoBinario.clientes;
    private Set<String> usuariosCadastrados = new HashSet<>();
    private int proximoNumeroConta = ArquivoBinario.proximoNumeroConta;

    public BancoController() {
        for (Cliente cliente : clientes.values()) {
            usuariosCadastrados.add(normalizarUsuario(cliente.getUsuario()));
        }
    }

    public Cliente cadastrar(String nome, String cpf, String usuario, String senha) {
        if (!ValidadorCPF.isValido(cpf)) return null;

        cpf = ValidadorCPF.somenteNumeros(cpf);

        if (clientes.containsKey(cpf)
                || usuariosCadastrados.contains(normalizarUsuario(usuario))) return null;

        Cliente cliente = new Cliente(nome, cpf, usuario,
                ValidadorCPF.hashSenha(senha));

        String numero = String.format("%06d", proximoNumeroConta++);
        cliente.setConta(new ContaBancaria(numero));
        clientes.put(cpf, cliente);
        usuariosCadastrados.add(normalizarUsuario(usuario));

        ArquivoBinario.proximoNumeroConta = proximoNumeroConta;
        return cliente;
    }

    public Cliente login(String usuario, String senha) {
        Cliente cliente = buscarPorUsuario(usuario);
        if (cliente == null) return null;

        String hash = ValidadorCPF.hashSenha(senha);
        return cliente.getSenhaHash().equals(hash) ? cliente : null;
    }

    private Cliente buscarPorUsuario(String usuario) {
        for (Cliente cliente : clientes.values()) {
            if (cliente.getUsuario().equalsIgnoreCase(usuario)) return cliente;
        }
        return null;
    }

    private String normalizarUsuario(String usuario) {
        return usuario == null ? null : usuario.toLowerCase(Locale.ROOT);
    }

    public Cliente buscarPorCpf(String cpf) {
        return clientes.get(ValidadorCPF.somenteNumeros(cpf));
    }

    public Map<String, Cliente> getClientes() {
        return clientes;
    }
}

import { useState } from "react";

import Modal from "../../components/Modal";

export default function ProfessorFormModal({
  professor,
  processando,
  erro,
  onClose,
  onSalvar,
}) {
  const editando = Boolean(professor);

  const [nome, setNome] = useState(professor?.nome || "");
  const [email, setEmail] = useState(professor?.email || "");
  const [senha, setSenha] = useState("");

  async function enviar(event) {
    event.preventDefault();

    const dados = {
      nome: nome.trim(),
      email: email.trim(),
    };

    if (!editando) {
      dados.senha = senha;
    }

    await onSalvar(dados);
  }

  return (
    <Modal aberto={true} onClose={onClose}>
      <form className="prof-admin-formulario" onSubmit={enviar}>
        <h2>{editando ? "Editar professor" : "Novo professor"}</h2>

        <p>
          {editando
            ? "Atualize os dados do professor."
            : "Cadastre um professor para permitir acesso às reservas de salas."}
        </p>

        <label>
          Nome
          <input
            type="text"
            value={nome}
            onChange={(event) => setNome(event.target.value)}
            placeholder="Nome do professor"
            disabled={processando}
            autoFocus
          />
        </label>

        <label>
          E-mail
          <input
            type="email"
            value={email}
            onChange={(event) => setEmail(event.target.value)}
            placeholder="professor@email.com"
            disabled={processando}
          />
        </label>

        {!editando && (
          <label>
            Senha inicial
            <input
              type="password"
              value={senha}
              onChange={(event) => setSenha(event.target.value)}
              placeholder="Mínimo 6 caracteres"
              minLength={6}
              disabled={processando}
            />
          </label>
        )}

        {erro && <div className="prof-admin-form-erro">{erro}</div>}

        <div className="prof-admin-form-acoes">
          <button
            type="button"
            className="secundario"
            onClick={onClose}
            disabled={processando}
          >
            Cancelar
          </button>

          <button type="submit" className="principal" disabled={processando}>
            {processando
              ? "Salvando..."
              : editando
                ? "Salvar alterações"
                : "Cadastrar professor"}
          </button>
        </div>
      </form>
    </Modal>
  );
}

import { useState } from "react";

import Modal from "../../components/Modal";

export default function BlocoFormModal({
  bloco,
  processando,
  erro,
  onClose,
  onSalvar,
}) {
  const editando = Boolean(bloco);

  const [codigo, setCodigo] = useState(bloco?.codigo || "");

  async function enviar(event) {
    event.preventDefault();

    await onSalvar({
      codigo: codigo.trim().toUpperCase(),
    });
  }

  return (
    <Modal aberto={true} onClose={onClose}>
      <form className="admin-formulario" onSubmit={enviar}>
        <h2>{editando ? "Editar bloco" : "Novo bloco"}</h2>

        <p>
          {editando
            ? "Atualize o código do bloco."
            : "Informe o código do novo bloco."}
        </p>

        <label>
          Código do bloco
          <input
            type="text"
            value={codigo}
            onChange={(event) => setCodigo(event.target.value)}
            placeholder="Ex.: A"
            disabled={processando}
            autoFocus
          />
        </label>

        {erro && <div className="admin-form-erro">{erro}</div>}

        <div className="admin-form-acoes">
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
                : "Cadastrar bloco"}
          </button>
        </div>
      </form>
    </Modal>
  );
}

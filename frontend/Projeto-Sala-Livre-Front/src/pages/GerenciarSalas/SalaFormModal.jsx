import { useState } from "react";

import Modal from "../../components/Modal";

export default function SalaFormModal({
  sala,
  bloco,
  processando,
  erro,
  onClose,
  onSalvar,
}) {
  const editando = Boolean(sala);

  const [codigo, setCodigo] = useState(sala?.codigo || "");

  async function enviar(event) {
    event.preventDefault();

    await onSalvar({
      codigo: codigo.trim(),
    });
  }

  return (
    <Modal aberto={true} onClose={onClose}>
      <form className="admin-formulario" onSubmit={enviar}>
        <h2>{editando ? "Editar sala" : "Adicionar sala"}</h2>

        <p>
          {editando
            ? `Atualize o código da sala do Bloco ${bloco.codigo}.`
            : `A nova sala será cadastrada no Bloco ${bloco.codigo}.`}
        </p>

        <label>
          Código interno da sala
          <input
            type="text"
            value={codigo}
            onChange={(event) => setCodigo(event.target.value)}
            placeholder="Ex.: 101"
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
                : "Cadastrar sala"}
          </button>
        </div>
      </form>
    </Modal>
  );
}

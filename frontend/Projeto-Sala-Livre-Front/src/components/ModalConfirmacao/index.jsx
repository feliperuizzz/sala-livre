import { CheckCircle2, TriangleAlert } from "lucide-react";

import Modal from "../Modal";
import styles from "./style.module.css";

export default function ModalConfirmacao({
  aberto,
  titulo,
  mensagem,
  textoConfirmar = "Confirmar",
  textoCancelar = "Voltar",
  variante = "perigo",
  onConfirmar,
  onCancelar,
}) {
  if (!aberto) {
    return null;
  }

  const sucesso = variante === "sucesso";

  return (
    <Modal aberto={aberto} onClose={onCancelar}>
      <div className={styles.conteudo}>
        <div
          className={`${styles.icone} ${
            sucesso ? styles.iconeSucesso : styles.iconePerigo
          }`}
        >
          {sucesso ? (
            <CheckCircle2 size={26} strokeWidth={2.1} />
          ) : (
            <TriangleAlert size={26} strokeWidth={2.1} />
          )}
        </div>

        <h2 className={styles.titulo}>{titulo}</h2>

        <p className={styles.mensagem}>{mensagem}</p>

        <div className={styles.acoes}>
          <button type="button" className={styles.voltar} onClick={onCancelar}>
            {textoCancelar}
          </button>

          <button
            type="button"
            className={`${styles.confirmar} ${
              sucesso ? styles.confirmarSucesso : styles.confirmarPerigo
            }`}
            onClick={onConfirmar}
          >
            {textoConfirmar}
          </button>
        </div>
      </div>
    </Modal>
  );
}

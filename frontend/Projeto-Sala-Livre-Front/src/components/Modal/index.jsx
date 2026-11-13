import { useEffect } from "react";
import { X } from "lucide-react";

import styles from "./style.module.css";

export default function Modal({ aberto, onClose, children }) {
  useEffect(() => {
    if (!aberto) return;

    function fecharComEsc(event) {
      if (event.key === "Escape") {
        onClose();
      }
    }

    document.addEventListener("keydown", fecharComEsc);

    const overflowAnterior = document.body.style.overflow;
    document.body.style.overflow = "hidden";

    return () => {
      document.removeEventListener("keydown", fecharComEsc);
      document.body.style.overflow = overflowAnterior;
    };
  }, [aberto, onClose]);

  if (!aberto) {
    return null;
  }

  function clicarOverlay(event) {
    if (event.target === event.currentTarget) {
      onClose();
    }
  }

  return (
    <div className={styles.modalOverlay} onMouseDown={clicarOverlay}>
      <div
        className={styles.modalContainer}
        onMouseDown={(event) => event.stopPropagation()}
      >
        <button
          type="button"
          className={styles.modalFechar}
          onClick={onClose}
          aria-label="Fechar modal"
        >
          <X size={20} />
        </button>

        {children}
      </div>
    </div>
  );
}

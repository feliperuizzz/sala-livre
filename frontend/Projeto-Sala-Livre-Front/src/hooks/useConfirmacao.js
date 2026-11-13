import { useState } from "react";


const ESTADO_INICIAL = {
  aberto: false,

  titulo: "",

  mensagem: "",

  textoConfirmar: "Confirmar",

  textoCancelar: "Voltar",

  onConfirmar: null,
};


export function useConfirmacao() {

  const [
    confirmacao,
    setConfirmacao,
  ] = useState(ESTADO_INICIAL);


  function abrirConfirmacao({
    titulo,
    mensagem,
    textoConfirmar = "Confirmar",
    textoCancelar = "Voltar",
    onConfirmar,
  }) {

    setConfirmacao({
      aberto: true,

      titulo,

      mensagem,

      textoConfirmar,

      textoCancelar,

      onConfirmar,
    });
  }


  function fecharConfirmacao() {

    setConfirmacao(
      ESTADO_INICIAL
    );
  }


  return {
    confirmacao,
    abrirConfirmacao,
    fecharConfirmacao,
  };
}
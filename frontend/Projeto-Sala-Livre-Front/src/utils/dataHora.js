export function obterDataAtual() {
  const hoje = new Date();

  const ano = hoje.getFullYear();
  const mes = String(
    hoje.getMonth() + 1
  ).padStart(2, "0");

  const dia = String(
    hoje.getDate()
  ).padStart(2, "0");

  return `${ano}-${mes}-${dia}`;
}


export function obterPeriodoAtual() {
  const hora = new Date().getHours();

  if (hora >= 6 && hora < 12) {
    return "MANHA";
  }

  if (hora >= 12 && hora < 18) {
    return "TARDE";
  }

  return "NOITE";
}


export function formatarData(data) {

  if (!data) {
    return "";
  }

  const [ano, mes, dia] =
    data.split("-");

  const dataLocal =
    new Date(
      Number(ano),
      Number(mes) - 1,
      Number(dia)
    );

  return new Intl.DateTimeFormat(
    "pt-BR",
    {
      day: "2-digit",
      month: "short",
      year: "numeric",
    }
  ).format(dataLocal);
}


export function formatarPeriodo(periodo) {

  const periodos = {
    MANHA: "Manhã",
    TARDE: "Tarde",
    NOITE: "Noite",
  };

  return periodos[periodo] || periodo;
}
import { forwardRef } from "react";

import DatePicker, { registerLocale } from "react-datepicker";

import { ptBR } from "date-fns/locale";
import { CalendarDays } from "lucide-react";

import "react-datepicker/dist/react-datepicker.css";

import styles from "./style.module.css";

registerLocale("pt-BR", ptBR);

function stringParaData(valor) {
  if (!valor) {
    return null;
  }

  const [ano, mes, dia] = valor.split("-").map(Number);

  return new Date(ano, mes - 1, dia);
}


function dataParaString(data) {
  if (!data) {
    return "";
  }

  const ano = data.getFullYear();

  const mes = String(data.getMonth() + 1).padStart(2, "0");

  const dia = String(data.getDate()).padStart(2, "0");

  return `${ano}-${mes}-${dia}`;
}


const CampoCalendario = forwardRef(function CampoCalendario(
  { value, onClick },
  ref,
) {
  return (
    <button type="button" ref={ref} className={styles.campo} onClick={onClick}>
      <CalendarDays size={18} />

      <span>{value}</span>
    </button>
  );
});

export default function SeletorData({ value, onChange, minDate }) {
  const dataSelecionada = stringParaData(value);

  const dataMinima = stringParaData(minDate);

  return (
    <div className={styles.container}>
      <DatePicker
        selected={dataSelecionada}
        onChange={(novaData) => {
          if (novaData) {
            onChange(dataParaString(novaData));
          }
        }}
        minDate={dataMinima}
        locale="pt-BR"
        dateFormat="dd MMM yyyy"
        customInput={<CampoCalendario />}
        calendarStartDay={0}
        showPopperArrow={false}
        popperPlacement="bottom-end"
        previousMonthButtonLabel="Mês anterior"
        nextMonthButtonLabel="Próximo mês"
      />
    </div>
  );
}

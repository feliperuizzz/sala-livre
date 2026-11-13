import api from "./api";

const salaService = {

  async consultarDisponibilidade(data, periodo) {
    const response = await api.get(
      "/salas/disponibilidade",
      {
        params: {
          data,
          periodo,
        },
      }
    );

    return response.data;
  },

};

export default salaService;
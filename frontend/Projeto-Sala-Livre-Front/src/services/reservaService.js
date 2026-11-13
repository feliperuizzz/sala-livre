import api from "./api";

const reservaService = {

  async reservar(salaId, data, periodo) {

    const response = await api.post(
      "/reservas",
      {
        salaId,
        data,
        periodo,
      }
    );

    return response.data;
  },


  async cancelar(reservaId) {

    await api.delete(
      `/reservas/${reservaId}`
    );
  },

};

export default reservaService;
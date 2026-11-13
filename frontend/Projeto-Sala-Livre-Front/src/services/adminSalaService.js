import api from "./api";

const adminSalaService = {
  async listarBlocos() {
    const response = await api.get(
      "/admin/blocos"
    );

    return response.data;
  },

  async cadastrarBloco(codigo) {
    const response = await api.post(
      "/admin/blocos",
      {
        codigo,
      }
    );

    return response.data;
  },

  async editarBloco(id, codigo) {
    const response = await api.put(
      `/admin/blocos/${id}`,
      {
        codigo,
      }
    );

    return response.data;
  },

  async excluirBloco(id) {
    await api.delete(
      `/admin/blocos/${id}`
    );
  },

  async listarSalas() {
    const response = await api.get(
      "/admin/salas"
    );

    return response.data;
  },

  async cadastrarSala(codigo, blocoId) {
    const response = await api.post(
      "/admin/salas",
      {
        codigo,
        blocoId,
      }
    );

    return response.data;
  },

  async editarSala(id, codigo) {
    const response = await api.put(
      `/admin/salas/${id}`,
      {
        codigo,
      }
    );

    return response.data;
  },

  async excluirSala(id) {
    await api.delete(
      `/admin/salas/${id}`
    );
  },
};

export default adminSalaService;
import api from "./api";

const adminProfessorService = {
  async listar() {
    const response = await api.get(
      "/admin/professores"
    );

    return response.data;
  },

  async cadastrar(nome, email, senha) {
    const response = await api.post(
      "/admin/professores",
      {
        nome,
        email,
        senha,
      }
    );

    return response.data;
  },

  async editar(id, dados) {
    const response = await api.put(
      `/admin/professores/${id}`,
      dados
    );

    return response.data;
  },

  async alterarSituacao(id) {
    const response = await api.patch(
      `/admin/professores/${id}/situacao`
    );

    return response.data;
  },
};

export default adminProfessorService;
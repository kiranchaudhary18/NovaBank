import { apiClient } from "../client";

export interface Statement {
  id: string;
  accountId: string;
  statementPeriod: string;
  statementDate: string;
  statementUrl: string;
  status: string;
}

export interface StatementPage {
  content: Statement[];
  totalElements: number;
  totalPages: number;
}

export const StatementService = {
  searchStatements: async (page = 0, size = 20): Promise<StatementPage> => {
    const { data } = await apiClient.get(`/statements?page=${page}&size=${size}`);
    return data.data;
  },
};

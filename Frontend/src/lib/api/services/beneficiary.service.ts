import { apiClient } from "../client";

export interface Beneficiary {
  id: string;
  name: string;
  accountNumber: string;
  bankName: string;
  ifscCode: string;
  status: string;
  isFavorite: boolean;
  relationship: string;
  nickname: string;
  customerId: string;
  createdAt: string;
}

export interface BeneficiaryPage {
  content: Beneficiary[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
}

export const BeneficiaryService = {
  searchBeneficiaries: async (page = 0, size = 20): Promise<BeneficiaryPage> => {
    const { data } = await apiClient.get(`/beneficiaries?page=${page}&size=${size}`);
    return data.data;
  },

  addBeneficiary: async (payload: { name: string; accountNumber: string; bankName: string; ifscCode: string; relationship: string; nickname?: string }): Promise<Beneficiary> => {
    const { data } = await apiClient.post("/beneficiaries", payload);
    return data.data;
  },
  
  toggleFavorite: async (id: string, isFavorite: boolean): Promise<Beneficiary> => {
    const { data } = await apiClient.patch(`/beneficiaries/${id}/favorite?isFavorite=${isFavorite}`);
    return data.data;
  }
};

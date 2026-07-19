import { apiClient } from "../client";

export interface CustomerProfile {
  id: string;
  userId: string;
  firstName: string;
  lastName: string;
  dateOfBirth: string;
  phoneNumber: string;
  gender: string;
  status: string;
  photoUrl: string;
}

export const CustomerService = {
  getMyProfile: async (): Promise<CustomerProfile> => {
    const { data } = await apiClient.get("/customers/me");
    return data.data;
  },
};

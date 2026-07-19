import { apiClient } from "../client";
import { API_CONFIG } from "../config";
import type { Account } from "../types";

export const AccountService = {
  async getMyAccounts(): Promise<Account[]> {

    
    // Spring Boot returns ApiResponse<List<AccountSummaryResponse>>
    const { data } = await apiClient.get("/accounts/summary");
    
    return data.data.map((acc: any) => ({
      id: acc.id,
      type: acc.accountType,
      accountNumber: acc.accountNumber,
      ifsc: "NOVA0001001", // Default mocked IFSC since backend doesn't provide it yet
      balance: acc.balance,
      currency: acc.currency,
      frozen: acc.status === "FROZEN",
    }));
  },
};

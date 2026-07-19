import { apiClient } from "../client";
import { API_CONFIG } from "../config";
import type { Transaction } from "../types";

export const TransactionService = {
  async getTransactions(accountNumber: string): Promise<Transaction[]> {

    
    // Spring Boot returns ApiResponse<PagedResponse<TransactionResponse>>
    const { data } = await apiClient.get(`/transactions/account/${accountNumber}`);
    
    return data.data.content.map((tx: any) => ({
      id: tx.id,
      accountId: accountNumber,
      type: tx.type, // CREDIT or DEBIT
      amount: tx.amount,
      currency: tx.currency || "USD",
      status: tx.status,
      merchant: tx.senderAccountNumber === accountNumber ? (tx.receiverAccountNumber || "External Transfer") : (tx.senderAccountNumber || "External Transfer"),
      category: tx.transactionType || "TRANSFER",
      createdAt: tx.transactionDate,
    }));
  },
};

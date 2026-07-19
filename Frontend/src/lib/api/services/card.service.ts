import { apiClient } from "../client";
import { API_CONFIG } from "../config";

export const CardService = {
  async getCards(accountNumber: string) {

    
    // Spring Boot returns ApiResponse<List<CardResponse>>
    const { data } = await apiClient.get(`/cards/account/${accountNumber}`);
    
    return data.data.map((card: any) => {
      let colorClass = "bg-gradient-to-br from-gray-800 to-black";
      if (card.cardType === "VIRTUAL") colorClass = "bg-gradient-to-br from-[#7C5CFF] to-[#6042db]";
      if (card.cardNetwork === "MASTERCARD") colorClass = "bg-gradient-to-br from-[#111111] to-[#000000]";
      
      return {
        id: card.id,
        type: card.cardType,
        brand: card.cardNetwork,
        last4: card.maskedCardNumber.slice(-4),
        cardholder: card.cardHolderName,
        expiry: card.expiryDate.substring(5, 7) + "/" + card.expiryDate.substring(2, 4), // MM/YY
        status: card.status,
        limit: card.dailyLimit,
        spent: 0, // Backend doesn't provide spent amount on card yet
        color: colorClass,
      };
    });
  },
};

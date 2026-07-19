// Shared DTOs for the Spring Boot API.

export interface User {
  id: string;
  fullName: string;
  email: string;
  phone?: string;
  avatarUrl?: string;
  role: "CUSTOMER" | "ADMIN" | "OPERATOR";
  createdAt: string;
}

export interface AuthTokens {
  accessToken: string;
  refreshToken: string;
  expiresIn: number;
}

export interface LoginPayload {
  email: string;
  password: string;
  remember?: boolean;
}

export interface SignupPayload {
  fullName: string;
  email: string;
  password: string;
}

export interface OtpPayload {
  email: string;
  code: string;
}

export interface Account {
  id: string;
  name?: string;
  type: "SAVINGS" | "CURRENT" | "FD" | string;
  accountNumber: string;
  ifsc: string;
  balance: number;
  currency: string;
  frozen: boolean;
  interestRate?: string;
}

export interface Transaction {
  id: string;
  accountId: string;
  type: "CREDIT" | "DEBIT";
  amount: number;
  currency: string;
  status: "SUCCESS" | "PENDING" | "FAILED";
  merchant: string;
  category: string;
  createdAt: string;
}

export interface Beneficiary {
  id: string;
  name: string;
  relationship: string;
  accountNumber: string;
  ifsc: string;
  bank: string;
}

export interface Card {
  id: string;
  type: "DEBIT" | "CREDIT";
  brand: "VISA" | "MASTERCARD" | "AMEX";
  last4: string;
  holder: string;
  expiry: string;
  frozen: boolean;
  limit: number;
  usage: number;
}

export interface Loan {
  id: string;
  type: string;
  amount: number;
  emi: number;
  tenureMonths: number;
  progress: number;
  status: "ACTIVE" | "CLOSED" | "PENDING";
}

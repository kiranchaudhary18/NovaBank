import { apiClient, tokenStore } from "../client";
import { API_CONFIG } from "../config";
import type { AuthTokens, LoginPayload, OtpPayload, SignupPayload, User } from "../types";

// Mock user for demo mode
const mockUser: User = {
  id: "usr_01HZX",
  fullName: "Ava Sterling",
  email: "ava@novabank.io",
  role: "CUSTOMER",
  avatarUrl: "",
  createdAt: new Date().toISOString(),
};

const mockTokens: AuthTokens = {
  accessToken: "mock.jwt.access.token",
  refreshToken: "mock.jwt.refresh.token",
  expiresIn: 3600,
};

const wait = (ms: number) => new Promise((r) => setTimeout(r, ms));

export const AuthService = {
  async login(payload: LoginPayload): Promise<{ user: User; tokens: AuthTokens }> {
    if (API_CONFIG.useMocks) {
      await wait(700);
      tokenStore.set(mockTokens.accessToken, mockTokens.refreshToken);
      return { user: { ...mockUser, email: payload.email }, tokens: mockTokens };
    }
    const { data } = await apiClient.post("/auth/login", payload);
    tokenStore.set(data.tokens.accessToken, data.tokens.refreshToken);
    return data;
  },

  async signup(payload: SignupPayload): Promise<{ user: User }> {
    if (API_CONFIG.useMocks) {
      await wait(900);
      return { user: { ...mockUser, ...payload } };
    }
    const { data } = await apiClient.post("/auth/signup", payload);
    return data;
  },

  async verifyOtp(payload: OtpPayload): Promise<{ tokens: AuthTokens }> {
    if (API_CONFIG.useMocks) {
      await wait(600);
      tokenStore.set(mockTokens.accessToken, mockTokens.refreshToken);
      return { tokens: mockTokens };
    }
    const { data } = await apiClient.post("/auth/verify-otp", payload);
    tokenStore.set(data.tokens.accessToken, data.tokens.refreshToken);
    return data;
  },

  async requestPasswordReset(email: string): Promise<void> {
    if (API_CONFIG.useMocks) return void (await wait(600));
    await apiClient.post("/auth/forgot-password", { email });
  },

  async resetPassword(payload: { email: string; code: string; password: string }): Promise<void> {
    if (API_CONFIG.useMocks) return void (await wait(600));
    await apiClient.post("/auth/reset-password", payload);
  },

  async me(): Promise<User> {
    if (API_CONFIG.useMocks) {
      await wait(200);
      return mockUser;
    }
    const { data } = await apiClient.get("/auth/me");
    return data;
  },

  logout() {
    tokenStore.clear();
  },
};

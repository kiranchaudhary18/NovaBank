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
    const { data: responseData } = await apiClient.post("/auth/login", payload);
    const authData = responseData.data;
    tokenStore.set(authData.token, "");
    return {
      user: authData.user,
      tokens: {
        accessToken: authData.token,
        refreshToken: "",
        expiresIn: authData.expiresIn || 3600,
      },
    };
  },

  async signup(payload: SignupPayload): Promise<{ user: User }> {
    if (API_CONFIG.useMocks) {
      await wait(900);
      return { user: { ...mockUser, ...payload } };
    }
    const { data: responseData } = await apiClient.post("/auth/register", {
      fullName: payload.fullName,
      email: payload.email,
      password: payload.password,
      phone: "+12345678900", // Default phone number since UI does not collect it
    });
    const authData = responseData.data;
    tokenStore.set(authData.token, "");
    return { user: authData.user };
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
    const { data } = await apiClient.get("/auth/me");
    return data.data;
  },

  logout() {
    tokenStore.clear();
  },
};

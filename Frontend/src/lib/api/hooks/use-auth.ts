import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { AuthService } from "../services/auth.service";
import type { LoginPayload, OtpPayload, SignupPayload } from "../types";

export function useCurrentUser() {
  return useQuery({
    queryKey: ["auth", "me"],
    queryFn: () => AuthService.me(),
    staleTime: 60_000,
  });
}

export function useLogin() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: (p: LoginPayload) => AuthService.login(p),
    onSuccess: (data) => qc.setQueryData(["auth", "me"], data.user),
  });
}

export function useSignup() {
  return useMutation({ mutationFn: (p: SignupPayload) => AuthService.signup(p) });
}

export function useVerifyOtp() {
  return useMutation({ mutationFn: (p: OtpPayload) => AuthService.verifyOtp(p) });
}

export function useRequestReset() {
  return useMutation({ mutationFn: (email: string) => AuthService.requestPasswordReset(email) });
}

export function useResetPassword() {
  return useMutation({
    mutationFn: (p: { email: string; code: string; password: string }) =>
      AuthService.resetPassword(p),
  });
}

import { apiClient } from "../../../lib/api-client";
import { sanitizeText } from "../../../lib/sanitize";
import type { UserAddress, UserProfile } from "../types/profile.types";

export interface AddressFormData {
  zipCode: string;
  street: string;
  number: string;
  complement: string;
  neighborhood: string;
  city: string;
  state: string;
  country: string;
}

const sanitizeAddress = (data: AddressFormData): AddressFormData => ({
  zipCode: sanitizeText(data.zipCode),
  street: sanitizeText(data.street),
  number: sanitizeText(data.number),
  complement: sanitizeText(data.complement),
  neighborhood: sanitizeText(data.neighborhood),
  city: sanitizeText(data.city),
  state: sanitizeText(data.state),
  country: sanitizeText(data.country),
});

export interface ChangePasswordData {
  currentPassword: string;
  newPassword: string;
}

export const profileApi = {
  async getProfile(userId: string): Promise<UserProfile> {
    const response = await apiClient.get<UserProfile>(`/users/${userId}`);
    return response.data;
  },

  async updateProfile(
    userId: string,
    data: { name: string },
  ): Promise<UserProfile> {
    const response = await apiClient.put<UserProfile>(`/users/${userId}`, data);
    return response.data;
  },

  async listAddresses(userId: string): Promise<UserAddress[]> {
    const response = await apiClient.get<UserAddress[]>(
      `/users/${userId}/addresses`,
    );
    return response.data;
  },

  async createAddress(
    userId: string,
    data: AddressFormData,
  ): Promise<UserAddress> {
    const response = await apiClient.post<UserAddress>(
      `/users/${userId}/addresses`,
      sanitizeAddress(data),
    );
    return response.data;
  },

  async updateAddress(
    userId: string,
    addressId: string,
    data: AddressFormData,
  ): Promise<UserAddress> {
    const response = await apiClient.put<UserAddress>(
      `/users/${userId}/addresses/${addressId}`,
      sanitizeAddress(data),
    );
    return response.data;
  },

  async changePassword(
    userId: string,
    data: ChangePasswordData,
  ): Promise<void> {
    await apiClient.patch(`/users/${userId}/password`, data);
  },
};

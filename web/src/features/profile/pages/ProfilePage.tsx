import { zodResolver } from "@hookform/resolvers/zod";
import { AlertCircle, ArrowLeft, CheckCircle } from "lucide-react";
import React, { useEffect, useState } from "react";
import { useForm } from "react-hook-form";
import { Navigate, useNavigate } from "react-router-dom";
import { Button } from "../../../components/ui/Button";
import { Input } from "../../../components/ui/Input";
import { useAuthStore } from "../../../stores/auth-store";
import { profileApi, type AddressFormData } from "../api/profile.api";
import { addressSchema } from "../schemas/address.schema";
import type { UserAddress } from "../types/profile.types";

export const ProfilePage: React.FC = () => {
  const navigate = useNavigate();
  const user = useAuthStore((state) => state.user);

  const [address, setAddress] = useState<UserAddress | null>(null);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [message, setMessage] = useState<string | null>(null);
  const [messageType, setMessageType] = useState<"success" | "error">(
    "success",
  );
  const [searchingZipCode, setSearchingZipCode] = useState(false);
  const [lookupError, setLookupError] = useState<string | null>(null);

  const {
    register,
    handleSubmit,
    reset,
    setValue,
    formState: { errors },
  } = useForm<AddressFormData>({
    resolver: zodResolver(addressSchema),
    defaultValues: {
      zipCode: "",
      street: "",
      number: "",
      complement: "",
      neighborhood: "",
      city: "",
      state: "",
      country: "Brasil",
    },
  });

  useEffect(() => {
    if (!user) {
      return;
    }

    profileApi
      .listAddresses(user.id)
      .then((addresses) => {
        const currentAddress = addresses[0] ?? null;
        setAddress(currentAddress);

        if (currentAddress) {
          reset({
            zipCode: currentAddress.zipCode,
            street: currentAddress.street,
            number: currentAddress.number,
            complement: currentAddress.complement ?? "",
            neighborhood: currentAddress.neighborhood,
            city: currentAddress.city,
            state: currentAddress.state,
            country: currentAddress.country,
          });
        }
      })
      .catch((error) => {
        setMessage(
          error.response?.data?.detail ??
            "Não foi possível carregar seu endereço.",
        );
        setMessageType("error");
      })
      .finally(() => setLoading(false));
  }, [user, reset]);

  if (!user) {
    return <Navigate to="/dashboard" replace />;
  }

  if (loading) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-background">
        <span className="w-8 h-8 border-4 border-primary border-t-transparent rounded-full animate-spin" />
      </div>
    );
  }

  const onSubmit = async (data: AddressFormData) => {
    if (!user || saving) return;

    setSaving(true);
    setMessage(null);

    try {
      const savedAddress = address
        ? await profileApi.updateAddress(user.id, address.id, data)
        : await profileApi.createAddress(user.id, data);

      setAddress(savedAddress);
      reset({
        zipCode: savedAddress.zipCode,
        street: savedAddress.street,
        number: savedAddress.number,
        complement: savedAddress.complement ?? "",
        neighborhood: savedAddress.neighborhood,
        city: savedAddress.city,
        state: savedAddress.state,
        country: savedAddress.country,
      });

      setMessage(
        "Endereço salvo com sucesso. Você receberá um e-mail de confirmação.",
      );
      setMessageType("success");
    } catch (error: any) {
      setMessage(
        error.response?.data?.detail ?? "Não foi possível salvar o endereço.",
      );
      setMessageType("error");
    } finally {
      setSaving(false);
    }
  };

  const handleZipCodeBlur = async (
    event: React.FocusEvent<HTMLInputElement>,
  ) => {
    const zipCode = event.target.value.replace(/\D/g, "");

    if (zipCode.length !== 8) return;

    setSearchingZipCode(true);
    setLookupError(null);

    try {
      const response = await fetch(`https://viacep.com.br/ws/${zipCode}/json/`);

      if (!response.ok) {
        throw new Error("Falha na consulta do CEP");
      }

      const data = await response.json();

      if (data.erro) {
        setLookupError("CEP não encontrado.");
        return;
      }

      setValue("zipCode", zipCode, { shouldValidate: true });
      setValue("street", data.logradouro ?? "", { shouldValidate: true });
      setValue("neighborhood", data.bairro ?? "", { shouldValidate: true });
      setValue("city", data.localidade ?? "", { shouldValidate: true });
      setValue("state", data.uf ?? "", { shouldValidate: true });
      setValue("country", "Brasil", { shouldValidate: true });
    } catch {
      setLookupError("Não foi possível consultar o CEP.");
    } finally {
      setSearchingZipCode(false);
    }
  };

  return (
    <main className="min-h-screen bg-background text-text-main p-6">
      <div className="max-w-3xl mx-auto">
        <button
          type="button"
          onClick={() => navigate(-1)}
          className="mb-4 inline-flex items-center gap-2 text-sm text-text-muted hover:text-primary"
        >
          <ArrowLeft className="w-4 h-4" />
          Voltar
        </button>

        <div className="bg-surface border border-border rounded-xl p-6 shadow-sm">
          <h1 className="text-2xl font-bold">Editar perfil</h1>
          <p className="text-sm text-text-muted mt-1">
            Atualize seu endereço de entrega.
          </p>

          {message && (
            <div
              className={`my-6 p-3 rounded-lg flex gap-2 text-sm ${
                messageType === "error"
                  ? "bg-danger/10 text-danger"
                  : "bg-success/10 text-success"
              }`}
            >
              {messageType === "error" ? (
                <AlertCircle className="w-5 h-5 shrink-0" />
              ) : (
                <CheckCircle className="w-5 h-5 shrink-0" />
              )}
              <span>{message}</span>
            </div>
          )}

          <form
            onSubmit={handleSubmit(onSubmit)}
            className="grid grid-cols-1 sm:grid-cols-2 gap-4 mt-6"
          >
            <Input
              label={searchingZipCode ? "CEP — consultando..." : "CEP"}
              inputMode="numeric"
              autoComplete="postal-code"
              error={errors.zipCode?.message ?? lookupError ?? undefined}
              {...register("zipCode", { onBlur: handleZipCodeBlur })}
            />

            <Input
              label="Rua"
              error={errors.street?.message}
              {...register("street")}
            />
            <Input
              label="Número"
              error={errors.number?.message}
              {...register("number")}
            />
            <Input
              label="Complemento"
              error={errors.complement?.message}
              {...register("complement")}
            />
            <Input
              label="Bairro"
              error={errors.neighborhood?.message}
              {...register("neighborhood")}
            />
            <Input
              label="Cidade"
              error={errors.city?.message}
              {...register("city")}
            />
            <Input
              label="Estado"
              error={errors.state?.message}
              {...register("state")}
            />
            <Input
              label="País"
              error={errors.country?.message}
              {...register("country")}
            />

            <div className="sm:col-span-2">
              <Button
                type="submit"
                isLoading={saving}
                disabled={searchingZipCode}
              >
                {address ? "Atualizar endereço" : "Adicionar endereço"}
              </Button>
            </div>
          </form>
        </div>
      </div>
    </main>
  );
};

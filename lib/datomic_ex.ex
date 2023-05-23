defmodule DatomicEx do
  @moduledoc """
  Documentation for `DatomicEx`.
  """

  def transact(tx_data) do
    datomic_call(:transact, tx_data)
  end

  def query(query_data) do
    datomic_call(:q, query_data)
  end

  @spec datomic_call(atom(), Enum.t()) :: {:error, Enum.t()} | {:ok, Enum.t()}
  def datomic_call(function, content) do
    task =
      Task.async(fn ->
        send({:datomic_mailbox, :clj_node@matheus}, %{
          "datomic/function": function,
          "source/from": self(),
          data: content
        })

        receive do
          msg -> msg
        end
      end)

    case Task.await(task) do
      {:ok, _} = success -> success
      {:error, _} = error -> error
    end
  end
end

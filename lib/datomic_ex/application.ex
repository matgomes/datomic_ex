defmodule DatomicEx.Application do
  # See https://hexdocs.pm/elixir/Application.html
  # for more information on OTP Applications
  @moduledoc false

  use Application

  @impl true
  def start(_type, _args) do
    :erlang.set_cookie(node(), :mycookie)
    Node.ping(:javaNode@matheus)

    children = [
      # Starts a worker by calling: DatomicEx.Worker.start_link(arg)
      # {DatomicEx.Worker, arg}
    ]

    opts = [strategy: :one_for_one, name: DatomicEx.Supervisor]
    Supervisor.start_link(children, opts)
  end
end

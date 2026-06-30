# Égide.IA - Motor NLP (Microsserviço de Anonimização)

Este é o microsserviço em Python construído com FastAPI e LangGraph, responsável pela pseudonimização inteligente de manifestações utilizando Inteligência Artificial, atendendo aos requisitos da LGPD.

Siga o passo a passo abaixo para rodar o projeto localmente.

## Pré-requisitos
* Python instalado (Recomendado 3.10 ou superior).

## Passo a Passo para Execução

### 1. Criar o Ambiente Virtual (venv)
Abra o terminal na pasta raiz do projeto e execute o comando abaixo para criar um ambiente isolado para as bibliotecas:

```python -m venv venv```

```.\venv\Scripts\activate```

```pip install -r requirements.txt```

Na raiz do projeto (no mesmo nível da pasta app e do arquivo requirements.txt), crie um arquivo com o exato nome .env

```bash
PROJECT_NAME="Égide.IA MS"
API_V1_STR="/api/v1"
ENVIRONMENT="development"

LLM_PROVIDER=groq # flag com a llm usada
GROQ_API_KEY="chave-secreta"
GROQ_MODEL="openai/gpt-oss-120b"
GOOGLE_API_KEY=""
GOOGLE_MODEL=""

```

Para rodar:

```uvicorn app.main:app --reload```

Acesso em:

```http://localhost:8000/docs```

No swagger, em authorize, colocar:

```chave_secreta_egide```
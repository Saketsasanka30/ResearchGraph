package com.example.researchgraph.data

import com.example.researchgraph.model.*

object DemoData {

    val collections = listOf(
        PaperCollection(
            id = "col-foundational",
            name = "Foundational Architecture",
            description = "Core transformer and representation learning milestones",
            iconName = "Layers",
            colorHex = "#4F46E5",
            count = 4
        ),
        PaperCollection(
            id = "col-rag",
            name = "RAG & Grounding",
            description = "Retrieval, factual grounding, and parametric memory",
            iconName = "Search",
            colorHex = "#0D9488",
            count = 3
        ),
        PaperCollection(
            id = "col-gnn",
            name = "Knowledge Graphs & GNNs",
            description = "Relational inductive bias and graph neural synthesis",
            iconName = "Share",
            colorHex = "#9333EA",
            count = 3
        ),
        PaperCollection(
            id = "col-quantum",
            name = "Quantum & Physics ML",
            description = "Many-body simulations and variational algorithms",
            iconName = "AutoAwesome",
            colorHex = "#D97706",
            count = 2
        )
    )

    val projects = listOf(
        Project(
            id = "proj-llm-kg",
            name = "Hybrid LLM + Knowledge Graphs",
            description = "Investigating bidirectional synthesis of parametric transformer knowledge and non-parametric relational knowledge graphs.",
            colorHex = "#4F46E5",
            paperCount = 3,
            notesCount = 4
        ),
        Project(
            id = "proj-quantum-sim",
            name = "Quantum Many-Body Modeling",
            description = "Tensor networks, QAOA benchmarks, and noisy intermediate-scale quantum simulation limits.",
            colorHex = "#D97706",
            paperCount = 1,
            notesCount = 2
        ),
        Project(
            id = "proj-neuro-plasticity",
            name = "Neuromorphic Memory Consolidation",
            description = "Synaptic weight stabilization and hippocampal replay analogs in modern artificial neural networks.",
            colorHex = "#0D9488",
            paperCount = 1,
            notesCount = 3
        )
    )

    val papers: List<Paper> = listOf(
        Paper(
            id = "paper-transformer",
            title = "Attention Is All You Need: Modern Evolution and Scaling Laws",
            authors = listOf("Ashish Vaswani", "Noam Shazeer", "Niki Parmar", "Jakob Uszkoreit", "Llion Jones", "Aidan N. Gomez", "Lukasz Kaiser", "Illia Polosukhin"),
            publicationVenue = "Advances in Neural Information Processing Systems (NeurIPS)",
            year = 2023,
            doi = "10.48550/arXiv.1706.03762",
            url = "https://arxiv.org/abs/1706.03762",
            abstractText = "The dominant sequence transduction models were based on complex recurrent or convolutional neural networks that include an encoder and a decoder. We evaluate the Transformer architecture based solely on attention mechanisms, dispensing with recurrence and convolutions entirely. Experiments show these models to be superior in quality while being more parallelizable and requiring significantly less time to train.",
            sections = listOf(
                Section(
                    id = "sec-t1",
                    title = "Abstract & Executive Summary",
                    content = "The Transformer replaces traditional recurrent architectures with multi-head self-attention. By discarding sequential step-by-step processing, training throughput increases by orders of magnitude on modern hardware accelerators. In this updated review, we trace empirical scaling laws from 100M parameters to over 1T parameters, establishing exact power-law frontiers between compute budget, dataset tokens, and cross-entropy loss.",
                    sectionType = "Abstract"
                ),
                Section(
                    id = "sec-t2",
                    title = "1. Introduction & Background",
                    content = "Recurrent models generate sequences conditioned on hidden states h_t from preceding token h_{t-1}. This inherently serial nature precludes parallel computing within training examples, which becomes critical at long sequence lengths. Attention mechanisms have become an integral part of compelling sequence modeling, allowing modeling of dependencies without regard to their distance in the input or output sequences.",
                    sectionType = "Introduction"
                ),
                Section(
                    id = "sec-t3",
                    title = "2. Scaled Dot-Product and Multi-Head Attention",
                    content = "We compute the attention function on a set of queries, keys, and values packed into matrices Q, K, and V:\n\nAttention(Q, K, V) = softmax(Q K^T / sqrt(d_k)) V\n\nRather than performing a single attention function with d_model dimensional keys, values and queries, we linearly project queries, keys, and values h times with different, learned linear projections to d_k, d_k and d_v dimensions respectively. Multi-head attention enables the model to jointly attend to information from different representation subspaces at different positions.",
                    sectionType = "Methodology"
                ),
                Section(
                    id = "sec-t4",
                    title = "3. Empirical Scaling & Computational Complexity",
                    content = "Self-attention layers connect all positions with a constant number of sequentially executed operations O(1), whereas recurrent layers require O(n) sequential operations. In terms of computational complexity per layer, self-attention requires O(n^2 * d), which is faster than recurrent layers when sequence length n is smaller than representation dimensionality d.",
                    sectionType = "Results"
                ),
                Section(
                    id = "sec-t5",
                    title = "4. Discussion, Open Limitations & Future Frontiers",
                    content = "While the quadratic cost O(n^2) was initially manageable for context lengths of 512 tokens, modern million-token contexts demand flash-attention kernel fusion, ring-attention distributed topologies, and sparse linear approximations. The interaction between non-parametric external retrieval and internal attention remains an active research challenge.",
                    sectionType = "Discussion"
                ),
                Section(
                    id = "sec-t6",
                    title = "5. Key Citations & Historical Influences",
                    content = "1. Hochreiter & Schmidhuber (1997) Long Short-Term Memory.\n2. Bahdanau et al. (2014) Neural Machine Translation by Jointly Learning to Align and Translate.\n3. Ba et al. (2016) Layer Normalization.\n4. Dao et al. (2022) FlashAttention: Fast and Memory-Efficient Exact Attention with IO-Awareness.",
                    sectionType = "References"
                )
            ),
            tags = listOf("Transformers", "Self-Attention", "Deep Learning", "NLP"),
            collectionIds = listOf("col-foundational"),
            projectId = "proj-llm-kg",
            citationCount = 112400,
            isBookmarked = true,
            readPercentage = 0.85f,
            dateAdded = System.currentTimeMillis() - 86400000L * 14
        ),
        Paper(
            id = "paper-gnn",
            title = "Graph Neural Networks for Scientific Discovery and Knowledge Synthesis",
            authors = listOf("William L. Hamilton", "Rex Ying", "Jure Leskovec"),
            publicationVenue = "IEEE Transactions on Pattern Analysis and Machine Intelligence",
            year = 2024,
            doi = "10.1109/TPAMI.2023.319082",
            url = "https://arxiv.org/abs/2003.00982",
            abstractText = "Relational data structures are ubiquitous across biology, chemistry, and citation topologies. We review the foundational principles of Message Passing Neural Networks (MPNNs), higher-order Weisfeiler-Lehman expressivity, and recent breakthroughs incorporating structural inductive biases for knowledge graph completion and cross-domain hypothesis generation.",
            sections = listOf(
                Section(
                    id = "sec-g1",
                    title = "Abstract & Core Hypotheses",
                    content = "Graphs represent complex relational dependencies that standard Euclidean deep learning struggles to encode. We formulate graph neural architectures via local permutation-invariant message aggregation across node neighborhoods, providing strong inductive priors for drug discovery, material design, and citation network mapping.",
                    sectionType = "Abstract"
                ),
                Section(
                    id = "sec-g2",
                    title = "1. Message Passing Formalism",
                    content = "In each iteration k of message passing, node representation h_v^{(k)} is updated according to:\n\nm_v^{(k)} = AGGREGATE^{(k)} ({ h_u^{(k-1)} : u in N(v) })\nh_v^{(k)} = UPDATE^{(k)} (h_v^{(k-1)}, m_v^{(k)})\n\nCommon aggregators include Mean, Sum, and Attention-weighted summation (GAT). We prove that sum-aggregators achieve 1-Weisfeiler-Lehman graph isomorphism discrimination equivalence.",
                    sectionType = "Methodology"
                ),
                Section(
                    id = "sec-g3",
                    title = "2. Knowledge Graph Embedding and Multi-Relational Links",
                    content = "When edges possess heterogeneous semantic relations r in R (e.g. 'cites', 'author_of', 'inhibits'), scoring functions f(u, r, v) like RotatE and ComplEx capture symmetry, antisymmetry, inversion, and composition patterns in low-dimensional complex vector spaces.",
                    sectionType = "Results"
                ),
                Section(
                    id = "sec-g4",
                    title = "3. Hybrid Graph-LLM Synergies",
                    content = "Recent experimental findings indicate that injecting graph topology embeddings into language model prompt representations reduces hallucination in multi-hop factual reasoning tasks by 41.6% compared to dense semantic vector retrieval alone.",
                    sectionType = "Discussion"
                )
            ),
            tags = listOf("Knowledge Graphs", "GNN", "Relational AI", "Bioinformatics"),
            collectionIds = listOf("col-gnn"),
            projectId = "proj-llm-kg",
            citationCount = 18450,
            isBookmarked = true,
            readPercentage = 0.60f,
            dateAdded = System.currentTimeMillis() - 86400000L * 10
        ),
        Paper(
            id = "paper-rag",
            title = "Retrieval-Augmented Generation for Autonomous Research Workspaces",
            authors = listOf("Patrick Lewis", "Ethan Perez", "Aleksandra Piktus", "Fabio Petroni", "Vladimir Karpukhin"),
            publicationVenue = "International Conference on Learning Representations (ICLR)",
            year = 2024,
            doi = "10.48550/arXiv.2005.11401",
            url = "https://arxiv.org/abs/2005.11401",
            abstractText = "Large language models store vast amounts of factual knowledge in their internal weights, but lack provenance, citation grounding, and temporal adaptability. We explore hybrid parametric/non-parametric models combining a pre-trained sequence-to-sequence model with a dense neural retriever over scientific corpuses.",
            sections = listOf(
                Section(
                    id = "sec-r1",
                    title = "Abstract & Problem Statement",
                    content = "Parametric memory in neural weights suffers from hallucination, catastrophic forgetting of updated research claims, and inability to trace exact literature provenance. Retrieval-Augmented Generation (RAG) decouples world knowledge storage into a dense vector index while utilizing the generative model purely as a contextual reasoner.",
                    sectionType = "Abstract"
                ),
                Section(
                    id = "sec-r2",
                    title = "1. Dense Passage Retrieval (DPR) Mechanism",
                    content = "Given input research query q and document chunk d, dual bi-encoders compute similarity:\n\nsim(q, d) = E_Q(q)^T E_D(d)\n\nTop-k relevant documents are retrieved via Hierarchical Navigable Small World (HNSW) approximate nearest neighbors and prepended into the decoder context window with exact span citations.",
                    sectionType = "Methodology"
                ),
                Section(
                    id = "sec-r3",
                    title = "2. Academic Verification & Hallucination Mitigation",
                    content = "On biomedical and arXiv QA benchmarks, RAG models achieve 84.3% factual accuracy with verbatim verifiable citations, outperforming raw parametric models of 10x parameter scale that attained only 58.1% factual fidelity.",
                    sectionType = "Results"
                )
            ),
            tags = listOf("RAG", "Retrieval", "Information Extraction", "Factuality"),
            collectionIds = listOf("col-rag"),
            projectId = "proj-llm-kg",
            citationCount = 8920,
            isBookmarked = false,
            readPercentage = 0.40f,
            dateAdded = System.currentTimeMillis() - 86400000L * 7
        ),
        Paper(
            id = "paper-quantum",
            title = "Quantum Advantage in Complex Many-Body Simulators",
            authors = listOf("John Preskill", "Frank Arute", "Kunal Arya", "Hartmut Neven"),
            publicationVenue = "Nature Physics & Quantum Science",
            year = 2023,
            doi = "10.1038/s41567-022-01899-x",
            url = "https://nature.com/articles/s41567-022-01899-x",
            abstractText = "Simulating quantum dynamics in strongly correlated lattice systems requires exponential Hilbert space resources on classical supercomputers. We demonstrate programmable superconducting processor circuits simulating 2D frustrated Heisenberg spin models with 67 qubits, executing analog evolution impossible under classical tensor-network contractions.",
            sections = listOf(
                Section(
                    id = "sec-q1",
                    title = "Abstract & Experimental Scope",
                    content = "Strongly correlated electron systems hold keys to high-temperature superconductivity and exotic topological phases. We map Hamiltonian time-evolution onto low-depth trotterized gate sequences, measuring non-local order parameters across thermalization transitions.",
                    sectionType = "Abstract"
                ),
                Section(
                    id = "sec-q2",
                    title = "1. Quantum Circuit Architecture",
                    content = "The processor utilizes transmon qubits arranged in a square grid with tunable capacitive couplers. Two-qubit cross-resonance gates achieve 99.4% median fidelity with randomized benchmarking calibration.",
                    sectionType = "Methodology"
                ),
                Section(
                    id = "sec-q3",
                    title = "2. Cross-Verification with DMRG",
                    content = "For 1D chains, results precisely match Density Matrix Renormalization Group (DMRG) classical calculations. When extended to 2D frustrated Kagome lattices, the quantum device maintains fidelity beyond classical matrix product state (MPS) bond dimension truncation limits.",
                    sectionType = "Results"
                )
            ),
            tags = listOf("Quantum Computing", "Many-Body Physics", "Superconducting Qubits"),
            collectionIds = listOf("col-quantum"),
            projectId = "proj-quantum-sim",
            citationCount = 4210,
            isBookmarked = true,
            readPercentage = 0.30f,
            dateAdded = System.currentTimeMillis() - 86400000L * 4
        ),
        Paper(
            id = "paper-neuro",
            title = "Neuroplasticity and Long-Term Memory Consolidation Mechanisms",
            authors = listOf("Eric Kandel", "Edvard I. Moser", "May-Britt Moser", "Susumu Tonegawa"),
            publicationVenue = "Cell & Nature Reviews Neuroscience",
            year = 2024,
            doi = "10.1016/j.cell.2023.11.018",
            url = "https://cell.com/action/neuroscience",
            abstractText = "Memory consolidation transforms labile short-term memory traces into stable long-term engrams through coordinated hippocampal-neocortical sharp-wave ripple replays and dendritic spine structural remodeling mediated by CREB transcription factor cascades.",
            sections = listOf(
                Section(
                    id = "sec-n1",
                    title = "Abstract & Biological Framework",
                    content = "How biological neural circuits prevent catastrophic forgetting while continuously encoding novel experiences represents one of neuroscience's deepest questions. We elucidate the molecular machinery of late-phase Long-Term Potentiation (L-LTP) and offline hippocampal ripple replay.",
                    sectionType = "Abstract"
                ),
                Section(
                    id = "sec-n2",
                    title = "1. Dual-Trace Hypothesis & Engram Reactivation",
                    content = "During slow-wave sleep, optogenetic tagging reveals synchronized burst firing in CA3/CA1 pyramidal ensembles that mirror awake exploration trajectories at 15x time compression, driving synaptic stabilization in anterior cingulate cortex.",
                    sectionType = "Methodology"
                ),
                Section(
                    id = "sec-n3",
                    title = "2. Implications for Artificial Continual Learning",
                    content = "Computational models adopting dual-rate plastic weights (fast episodic buffer + slow semantic neocortex) eliminate catastrophic forgetting in deep reinforcement learning agents facing non-stationary task distributions.",
                    sectionType = "Discussion"
                )
            ),
            tags = listOf("Neuroscience", "Memory Consolidation", "Plasticity", "Continual Learning"),
            collectionIds = listOf("col-foundational"),
            projectId = "proj-neuro-plasticity",
            citationCount = 7650,
            isBookmarked = false,
            readPercentage = 0.15f,
            dateAdded = System.currentTimeMillis() - 86400000L * 2
        )
    )

    val sources: List<Source> = listOf(
        Source(
            id = "src-1",
            title = "Attention Is All You Need (NeurIPS 2023 Edition)",
            type = SourceType.PAPER,
            authorsOrSource = "Vaswani et al.",
            url = "https://arxiv.org/abs/1706.03762",
            summary = "Seminal transformer architecture replacing recurrence with multi-head attention.",
            tags = listOf("Deep Learning", "Transformer", "Attention"),
            collectionId = "col-foundational",
            projectId = "proj-llm-kg",
            linkedPaperId = "paper-transformer"
        ),
        Source(
            id = "src-2",
            title = "GNNs for Scientific Discovery & Synthesis",
            type = SourceType.PAPER,
            authorsOrSource = "Hamilton, Ying, Leskovec",
            url = "https://arxiv.org/abs/2003.00982",
            summary = "Comprehensive foundation on message-passing graph neural networks and inductive reasoning.",
            tags = listOf("Knowledge Graphs", "GNN"),
            collectionId = "col-gnn",
            projectId = "proj-llm-kg",
            linkedPaperId = "paper-gnn"
        ),
        Source(
            id = "src-3",
            title = "Retrieval-Augmented Generation Whitepaper",
            type = SourceType.PDF,
            authorsOrSource = "Lewis et al. (Meta AI Research)",
            url = "https://arxiv.org/pdf/2005.11401.pdf",
            summary = "Parametric and non-parametric memory combination for verifiable scientific answering.",
            tags = listOf("RAG", "Retrieval", "Verification"),
            collectionId = "col-rag",
            projectId = "proj-llm-kg",
            linkedPaperId = "paper-rag"
        ),
        Source(
            id = "src-4",
            title = "Stanford CS224W: Machine Learning with Graphs Lecture Notes",
            type = SourceType.DOCUMENT,
            authorsOrSource = "Stanford University (Jure Leskovec)",
            url = "https://web.stanford.edu/class/cs224w/",
            summary = "Course curriculum covering Weisfeiler-Lehman expressivity bounds, GNN design spaces, and KG embeddings.",
            tags = listOf("Lecture", "Coursework", "GNN"),
            collectionId = "col-gnn",
            projectId = "proj-llm-kg"
        ),
        Source(
            id = "src-5",
            title = "Nature News: The Quantum Computing Race Reaches 1,000 Qubits",
            type = SourceType.URL,
            authorsOrSource = "Nature Journal Editorial Staff",
            url = "https://nature.com/articles/d41586-023-03859-8",
            summary = "Analysis of physical qubit coherence times vs logical fault-tolerant surface codes.",
            tags = listOf("Quantum", "Hardware", "News"),
            collectionId = "col-quantum",
            projectId = "proj-quantum-sim"
        ),
        Source(
            id = "src-6",
            title = "Synaptic Tagging and Capture Experimental Lab Protocol",
            type = SourceType.NOTE,
            authorsOrSource = "Internal Lab Research Memo",
            url = "lab://protocols/neuro-synaptic-tagging",
            summary = "Detailed protocol on verifying dendritic protein synthesis during hippocampal slice stimulation.",
            tags = listOf("Lab Protocol", "Neuroscience", "Experiment"),
            projectId = "proj-neuro-plasticity"
        ),
        Source(
            id = "src-7",
            title = "Bahdanau et al. (2014) Neural Machine Translation by Aligning",
            type = SourceType.REFERENCE,
            authorsOrSource = "Bahdanau, Cho, Bengio",
            url = "https://arxiv.org/abs/1409.0473",
            summary = "The original additive soft attention mechanism antecedent to multi-head dot product.",
            tags = listOf("Reference", "Attention", "History"),
            collectionId = "col-foundational",
            projectId = "proj-llm-kg"
        )
    )

    val notes: List<ResearchNote> = listOf(
        ResearchNote(
            id = "note-1",
            title = "Connecting Multi-Head Attention to Graph Attention (GAT)",
            content = "Self-attention can be formalized as a complete graph where every token connects to every other token with edge weights defined by softmax(QK^T / sqrt(d)). In GNNs, this is an attention mechanism restricted to the 1-hop adjacency matrix A. By constraining attention through knowledge graph edges, we can enforce factual reasoning and prevent unbounded hallucinations!",
            tags = listOf("Theory", "GNN", "Transformers"),
            paperId = "paper-transformer",
            paperTitle = "Attention Is All You Need",
            sectionTitle = "2. Scaled Dot-Product and Multi-Head Attention",
            highlightColor = "#9333EA",
            createdAt = System.currentTimeMillis() - 86400000L * 5,
            isPinned = true
        ),
        ResearchNote(
            id = "note-2",
            title = "Why RAG Needs Graph Structure (GraphRAG Hypothesis)",
            content = "Standard chunk-based vector similarity misses multi-hop relationships between entities dispersed across different document chapters. A knowledge graph preserves explicit relational paths like 'Compound X' -> 'inhibits' -> 'Enzyme Y' -> 'expressed in' -> 'Organ Z'. Graph traversals provide structured subgraphs that can be serialized as context.",
            tags = listOf("RAG", "GraphRAG", "Architecture"),
            paperId = "paper-rag",
            paperTitle = "Retrieval-Augmented Generation",
            sectionTitle = "2. Dense Passage Retrieval Mechanism",
            highlightColor = "#0D9488",
            createdAt = System.currentTimeMillis() - 86400000L * 3,
            isPinned = true
        ),
        ResearchNote(
            id = "note-3",
            title = "Biological Parallels to Vector Embedding Indexing",
            content = "Hippocampal CA3 acts very much like an approximate nearest neighbor index with pattern completion! High recurrence enables rapid attractor state retrieval from partial sensory cues. The neocortex then performs slow, batch-gradient optimization.",
            tags = listOf("Neuroscience", "Memory", "Architecture"),
            paperId = "paper-neuro",
            paperTitle = "Neuroplasticity and Long-Term Memory",
            sectionTitle = "1. Dual-Trace Hypothesis & Engram Reactivation",
            highlightColor = "#D97706",
            createdAt = System.currentTimeMillis() - 86400000L * 2,
            isPinned = false
        ),
        ResearchNote(
            id = "note-4",
            title = "Limitations of 1-WL Graph Isomorphism Bounds",
            content = "Standard MPNNs cannot distinguish strongly regular non-isomorphic graphs (likeDecagram vs two Pentagons). High-order subgraph GNNs or topological persistent homology features are required for molecular chirality.",
            tags = listOf("GNN", "Math", "Limits"),
            paperId = "paper-gnn",
            paperTitle = "Graph Neural Networks for Scientific Discovery",
            sectionTitle = "1. Message Passing Formalism",
            highlightColor = "#4F46E5",
            createdAt = System.currentTimeMillis() - 86400000L * 1,
            isPinned = false
        )
    )

    val annotations: List<PaperAnnotation> = listOf(
        PaperAnnotation(
            id = "ann-1",
            paperId = "paper-transformer",
            sectionId = "sec-t3",
            selectedText = "Multi-head attention enables the model to jointly attend to information from different representation subspaces at different positions.",
            note = "Key insight: single head averages out directional subspace nuances. Multiple heads act like parallel filter banks.",
            colorHex = "#FDE047"
        ),
        PaperAnnotation(
            id = "ann-2",
            paperId = "paper-transformer",
            sectionId = "sec-t4",
            selectedText = "In terms of computational complexity per layer, self-attention requires O(n^2 * d)",
            note = "Check FlashAttention-3 benchmark numbers to see if IO-awareness completely changes this bottleneck.",
            colorHex = "#99F6E4"
        ),
        PaperAnnotation(
            id = "ann-3",
            paperId = "paper-gnn",
            sectionId = "sec-g4",
            selectedText = "injecting graph topology embeddings into language model prompt representations reduces hallucination in multi-hop factual reasoning tasks by 41.6%",
            note = "Outstanding empirical result! Cite this in Chapter 3 literature review.",
            colorHex = "#FECDD3"
        )
    )

    val researchQuestions: List<ResearchQuestion> = listOf(
        ResearchQuestion(
            id = "rq-1",
            question = "Can non-parametric knowledge graph walks dynamically guide transformer attention mask sparsity during inference?",
            context = "Synthesizing Attention Is All You Need with GNN Message Passing. Instead of attending to all N tokens, only attend to tokens that share a verified knowledge graph path.",
            sourcePaperIds = listOf("paper-transformer", "paper-gnn"),
            keyHypothesis = "Graph-guided dynamic attention masks reduce O(n^2) computation to O(n * deg(G)) while cutting hallucinated entity links by over 60%.",
            suggestedMethod = "Train a lightweight relational GNN over extracted entity triples; output sparse attention mask tensors to transformer decoder layers.",
            status = "Investigating"
        ),
        ResearchQuestion(
            id = "rq-2",
            question = "How can hippocampal replay rhythms inspire continual learning without catastrophic forgetting in billion-parameter LLMs?",
            context = "Connecting Eric Kandel's neuroplasticity work with parametric memory decay during fine-tuning.",
            sourcePaperIds = listOf("paper-neuro", "paper-rag"),
            keyHypothesis = "Interleaving offline synthetic sleep replays of high-entropy knowledge graph subgraphs will preserve prior task weights with zero performance degradation.",
            suggestedMethod = "Simulate 15-minute scheduled consolidation cycles using LoRA low-rank delta matrices and generative replay.",
            status = "Open"
        ),
        ResearchQuestion(
            id = "rq-3",
            question = "What are the minimal quantum circuit depths required for non-perturbative simulation of biological ion channels?",
            context = "Evaluating Preskill's quantum advantage Hamiltonian simulator for membrane potential modeling.",
            sourcePaperIds = listOf("paper-quantum", "paper-neuro"),
            keyHypothesis = "Variational Quantum Eigensolver (VQE) with active-space selection achieves chemical accuracy with under 80 entangling CNOT gates.",
            suggestedMethod = "Benchmark against classic coupled-cluster CCSD(T) calculations on high-performance compute clusters.",
            status = "Open"
        )
    )

    // Knowledge Graph: Nodes & Interconnected Relational Edges
    // Positioned in a 800x800 coordinate virtual space
    val graphNodes: List<GraphNode> = listOf(
        // Papers
        GraphNode("paper-transformer", "Attention Is All You Need", "Vaswani et al. (2023)", NodeType.PAPER, 400f, 250f, 32f, "paper-transformer"),
        GraphNode("paper-gnn", "GNNs for Scientific Discovery", "Hamilton et al. (2024)", NodeType.PAPER, 240f, 440f, 30f, "paper-gnn"),
        GraphNode("paper-rag", "Retrieval-Augmented Generation", "Lewis et al. (2024)", NodeType.PAPER, 580f, 430f, 30f, "paper-rag"),
        GraphNode("paper-quantum", "Quantum Many-Body Simulator", "Preskill et al. (2023)", NodeType.PAPER, 150f, 650f, 28f, "paper-quantum"),
        GraphNode("paper-neuro", "Neuroplasticity & Memory", "Kandel et al. (2024)", NodeType.PAPER, 660f, 650f, 28f, "paper-neuro"),

        // Authors
        GraphNode("author-vaswani", "Ashish Vaswani", "Google Brain", NodeType.AUTHOR, 330f, 130f, 22f, "paper-transformer"),
        GraphNode("author-hamilton", "William Hamilton", "Stanford / McGill", NodeType.AUTHOR, 140f, 360f, 22f, "paper-gnn"),
        GraphNode("author-lewis", "Patrick Lewis", "Meta AI Research", NodeType.AUTHOR, 680f, 340f, 22f, "paper-rag"),
        GraphNode("author-preskill", "John Preskill", "Caltech Quantum", NodeType.AUTHOR, 70f, 540f, 22f, "paper-quantum"),
        GraphNode("author-kandel", "Eric Kandel", "Columbia Neuroscience", NodeType.AUTHOR, 750f, 560f, 22f, "paper-neuro"),

        // Concepts
        GraphNode("concept-self-attn", "Self-Attention Mechanism", "Core transformer building block", NodeType.CONCEPT, 410f, 100f, 24f, "paper-transformer"),
        GraphNode("concept-mpnn", "Message Passing (MPNN)", "Graph aggregation formalism", NodeType.CONCEPT, 310f, 540f, 24f, "paper-gnn"),
        GraphNode("concept-dpr", "Dense Passage Retrieval", "Dual-encoder similarity", NodeType.CONCEPT, 500f, 550f, 24f, "paper-rag"),
        GraphNode("concept-qaoa", "Hamiltonian Simulation", "Unitary quantum time evolution", NodeType.CONCEPT, 180f, 770f, 22f, "paper-quantum"),
        GraphNode("concept-replay", "Hippocampal Replay", "Offline memory consolidation", NodeType.CONCEPT, 630f, 770f, 22f, "paper-neuro"),
        GraphNode("concept-graphrag", "Graph-RAG Synthesis", "Emerging hybrid paradigm", NodeType.CONCEPT, 410f, 480f, 26f, null),

        // Topics
        GraphNode("topic-nlp", "Natural Language Processing", "Broad AI domain", NodeType.TOPIC, 540f, 160f, 24f, null),
        GraphNode("topic-relational", "Relational Learning", "Graph topologies & embeddings", NodeType.TOPIC, 220f, 240f, 24f, null),
        GraphNode("topic-cogsci", "Cognitive Neuroscience", "Biological computation", NodeType.TOPIC, 760f, 450f, 24f, null),

        // Citations / References
        GraphNode("cite-bahdanau", "Bahdanau Attention (2014)", "Foundational additive attention", NodeType.CITATION, 470f, 30f, 20f, null),
        GraphNode("cite-flashattn", "FlashAttention (Dao 2022)", "IO-aware GPU kernel fusion", NodeType.CITATION, 260f, 40f, 20f, null),
        GraphNode("cite-wl-test", "Weisfeiler-Lehman Test", "Graph isomorphism expressivity", NodeType.CITATION, 80f, 440f, 20f, null),

        // Notes
        GraphNode("node-note-1", "Note: GAT vs Self-Attention", "Relational attention theory", NodeType.NOTE, 290f, 330f, 18f, "paper-transformer"),
        GraphNode("node-note-2", "Note: Why RAG Needs Graphs", "Multi-hop graph traversal note", NodeType.NOTE, 520f, 340f, 18f, "paper-rag")
    )

    val graphEdges: List<GraphEdge> = listOf(
        // Transformer connections
        GraphEdge("e1", "paper-transformer", "author-vaswani", "written_by"),
        GraphEdge("e2", "paper-transformer", "concept-self-attn", "introduces"),
        GraphEdge("e3", "paper-transformer", "topic-nlp", "belongs_to"),
        GraphEdge("e4", "paper-transformer", "cite-bahdanau", "cites"),
        GraphEdge("e5", "paper-transformer", "cite-flashattn", "extended_by"),
        GraphEdge("e6", "paper-transformer", "node-note-1", "note_on"),

        // GNN connections
        GraphEdge("e7", "paper-gnn", "author-hamilton", "written_by"),
        GraphEdge("e8", "paper-gnn", "concept-mpnn", "formalizes"),
        GraphEdge("e9", "paper-gnn", "topic-relational", "belongs_to"),
        GraphEdge("e10", "paper-gnn", "cite-wl-test", "analyzes"),
        GraphEdge("e11", "paper-gnn", "concept-graphrag", "bridges_to"),

        // RAG connections
        GraphEdge("e12", "paper-rag", "author-lewis", "written_by"),
        GraphEdge("e13", "paper-rag", "concept-dpr", "utilizes"),
        GraphEdge("e14", "paper-rag", "paper-transformer", "builds_upon"),
        GraphEdge("e15", "paper-rag", "concept-graphrag", "bridges_to"),
        GraphEdge("e16", "paper-rag", "node-note-2", "note_on"),

        // Cross-domain synergies (GraphRAG connecting GNN, Transformer & RAG)
        GraphEdge("e17", "concept-graphrag", "paper-transformer", "augments"),
        GraphEdge("e18", "concept-graphrag", "paper-gnn", "integrates"),
        GraphEdge("e19", "concept-graphrag", "concept-self-attn", "constrains"),

        // Quantum connections
        GraphEdge("e20", "paper-quantum", "author-preskill", "written_by"),
        GraphEdge("e21", "paper-quantum", "concept-qaoa", "simulates"),

        // Neuroscience connections
        GraphEdge("e22", "paper-neuro", "author-kandel", "written_by"),
        GraphEdge("e23", "paper-neuro", "concept-replay", "discovers"),
        GraphEdge("e24", "paper-neuro", "topic-cogsci", "belongs_to"),
        GraphEdge("e25", "concept-replay", "paper-rag", "analogous_to"),

        // Notes connecting to ideas
        GraphEdge("e26", "node-note-1", "paper-gnn", "synthesizes"),
        GraphEdge("e27", "node-note-2", "concept-graphrag", "motivates"),

        // Topic links
        GraphEdge("e28", "topic-relational", "paper-transformer", "relational_formulation"),
        GraphEdge("e29", "topic-nlp", "paper-rag", "applied_in")
    )
}
